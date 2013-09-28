package cz.cuni.mff.xrg.odcs.frontend.browser;

import com.vaadin.data.Container;
import com.vaadin.ui.VerticalLayout;
import java.io.File;
import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.ContainerFactory;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.IntlibPagedTable;
import cz.cuni.mff.xrg.odcs.rdf.GraphUrl;
import cz.cuni.mff.xrg.odcs.rdf.data.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.impl.RDFTriple;
import cz.cuni.mff.xrg.odcs.rdf.impl.VirtuosoRDFRepo;

/**
 * Implementation of browser for
 * {@link cz.cuni.xrg.intlib.backend.data.rdf.LocalRDF}.
 *
 * @author Petyr
 *
 */
class VirtuosoRdfBrowser extends DataUnitBrowser {

	/**
	 * Data from repository.
	 */
	private List<RDFTriple> data = null;

	/**
	 * Table for data presentation.
	 */
	private IntlibPagedTable dataTable;

	@Override
	public void loadDataUnit(File directory, String dataUnitId) {
		AppConfig appConfig = App.getAppConfig();

		// load configuration from appConfig
		final String hostName = appConfig
				.getString(ConfigProperty.VIRTUOSO_HOSTNAME);
		final String port = appConfig.getString(ConfigProperty.VIRTUOSO_PORT);
		final String user = appConfig.getString(ConfigProperty.VIRTUOSO_USER);
		final String password = appConfig
				.getString(ConfigProperty.VIRTUOSO_PASSWORD);

		VirtuosoRDFRepo virtosoRepository = RDFDataUnitFactory
			.createVirtuosoRDFRepo(
				hostName,
				port,
				user,
				password,
				GraphUrl.translateDataUnitId(dataUnitId),
				"",
				App.getApp().getBean(AppConfig.class).getProperties()
			);

		data = virtosoRepository.getRDFTriplesInRepository();
		// close repository
		virtosoRepository.shutDown();
	}

	@Override
	public void enter() {
		VerticalLayout mainLayout = new VerticalLayout();
		loadBrowserTable(data);
		dataTable.setWidth("100%");
		dataTable.setHeight("100%");
		mainLayout.addComponent(dataTable);
		mainLayout.addComponent(dataTable.createControls());
		dataTable.setPageLength(17);
		setCompositionRoot(mainLayout);
	}

	private void loadBrowserTable(List<RDFTriple> data) {
		dataTable = new IntlibPagedTable();
		Container container = App.getApp().getBean(ContainerFactory.class).createRDFData(data);
		dataTable.setContainerDataSource(container);

		dataTable.setVisibleColumns("subject", "predicate", "object");
		dataTable.setFilterBarVisible(true);
	}
}
