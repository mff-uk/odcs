package gui;

import com.vaadin.data.Property;
import module.Config;

import com.vaadin.ui.*;

import cz.cuni.xrg.intlib.commons.configuration.*;
import cz.cuni.xrg.intlib.rdf.impl.SPARQLValidator;
import cz.cuni.xrg.intlib.rdf.interfaces.Validator;

/**
 * Configuration dialog.
 *
 * @author Maria
 *
 */
public class ConfigDialog extends CustomComponent {

	private static final long serialVersionUID = 1L;
	private GridLayout mainLayout;
	private TextArea txtQuery;
	private Label labelUpQuer;

	public ConfigDialog() {
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	/**
	 * Return current configuration from dialog. Can return null, if current
	 * configuration is invalid.
	 *
	 */
	public void getConfiguration(Configuration config) {
		config.setValue(Config.SPARQL_Update_Query.name(), txtQuery.getValue());
	}

	/**
	 * Load values from configuration into dialog.
	 *
	 * @throws ConfigurationException
	 * @param conf
	 */
	public void setConfiguration(Configuration conf) {
		try {
			txtQuery.setValue((String) conf.getValue(Config.SPARQL_Update_Query.name()));

		} catch (Exception ex) {
			// throw setting exception
			throw new ConfigurationException();
		}
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

				Validator validator = new SPARQLValidator(query);

				if (!validator.isQueryValid()) {
					
					Notification.show("Query Validator",
							"Query is not valid: "
							+ validator.getErrorMessage(),
							Notification.Type.ERROR_MESSAGE);
				}

			}
		});

		txtQuery.setNullRepresentation("");
		txtQuery.setImmediate(false);
		txtQuery.setWidth("100%");
		txtQuery.setHeight("211px");
		txtQuery.setInputPrompt("PREFIX br:<http://purl.org/business-register#>\nMODIFY\nDELETE { ?s pc:contact ?o}\nINSERT { ?s br:contact ?o}\nWHERE {\n\t     ?s a gr:BusinessEntity .\n\t      ?s pc:contact ?o\n}");
		mainLayout.addComponent(txtQuery, 1, 0);
		mainLayout.setColumnExpandRatio(0, 0.00001f);
		mainLayout.setColumnExpandRatio(1, 0.99999f);

		return mainLayout;
	}
}
