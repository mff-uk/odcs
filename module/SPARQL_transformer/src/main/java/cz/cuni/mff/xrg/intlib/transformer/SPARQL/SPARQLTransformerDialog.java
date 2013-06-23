package cz.cuni.mff.xrg.intlib.transformer.SPARQL;

import com.vaadin.data.Property;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.web.AbstractConfigDialog;

/**
 *
 * @author Jiri Tomes
 */
public class SPARQLTransformerDialog extends AbstractConfigDialog<SPARQLTransformerConfig> {

	private static final long serialVersionUID = 1L;

	private GridLayout mainLayout;

	private TextArea txtQuery;

	private Label labelUpQuer;

	public SPARQLTransformerDialog() {
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	private GridLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new GridLayout(2, 1);
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);
		//mainLayout.setSpacing(true);

		// top-level component properties
		setWidth("100%");
		setHeight("100%");

		// labelUpQuer
		labelUpQuer = new Label();
		labelUpQuer.setImmediate(false);
		labelUpQuer.setWidth("68px");
		labelUpQuer.setHeight("-1px");
		labelUpQuer.setValue("SPARQL  Update Query");
		mainLayout.addComponent(labelUpQuer, 0, 0);

		// textAreaUpQuer
		txtQuery = new TextArea();

		txtQuery.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				final String query = txtQuery.getValue();


			}
		});

		txtQuery.setNullRepresentation("");
		txtQuery.setImmediate(false);
		txtQuery.setWidth("100%");
		txtQuery.setHeight("211px");
		txtQuery.setInputPrompt(
				"PREFIX br:<http://purl.org/business-register#>\nMODIFY\nDELETE { ?s pc:contact ?o}\nINSERT { ?s br:contact ?o}\nWHERE {\n\t     ?s a gr:BusinessEntity .\n\t      ?s pc:contact ?o\n}");

		mainLayout.addComponent(txtQuery, 1, 0);
		mainLayout.setColumnExpandRatio(0, 0.00001f);
		mainLayout.setColumnExpandRatio(1, 0.99999f);

		return mainLayout;
	}

	@Override
	public void setConfiguration(SPARQLTransformerConfig conf) throws ConfigException {
		txtQuery.setValue(conf.SPARQL_Update_Query);
	}

	@Override
	public SPARQLTransformerConfig getConfiguration() throws ConfigException {

		//TODO Add Right SPARQL VALIDATOR
		if (!txtQuery.isValid()) {
			throw new ConfigException();
		} else {

			SPARQLTransformerConfig conf = new SPARQLTransformerConfig();
			conf.SPARQL_Update_Query = txtQuery.getValue();

			return conf;
		}
	}
}
