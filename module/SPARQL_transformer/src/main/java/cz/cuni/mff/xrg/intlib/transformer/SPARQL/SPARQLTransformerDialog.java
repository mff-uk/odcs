package cz.cuni.mff.xrg.intlib.transformer.SPARQL;

import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.*;

import cz.cuni.xrg.intlib.commons.configuration.DPUConfigObject;
import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.web.AbstractConfigDialog;
import cz.cuni.xrg.intlib.rdf.exceptions.SPARQLValidationException;
import cz.cuni.xrg.intlib.rdf.impl.SPARQLUpdateValidator;
import cz.cuni.xrg.intlib.rdf.interfaces.Validator;

/**
 * Configuration dialog for DPU SPARQL Transformer.
 *
 * @author Jiri Tomes
 * @author Maria Kukhar
 */
public class SPARQLTransformerDialog extends AbstractConfigDialog<SPARQLTransformerConfig> {

	private static final long serialVersionUID = 1L;

	private GridLayout mainLayout;

	private TextArea txtQuery;

	private Label labelUpQuer;

	/**
	 * Right SPARQL VALIDATOR - default false.
	 */
	private boolean isQueryValid = false;

	private InvalidValueException ex;

	private String errorMessage = "no errors";

	/**
	 * Basic constructor.
	 */
	public SPARQLTransformerDialog() {
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	/**
	 * Builds main layout with all dialog components.
	 *
	 * @return mainLayout GridLayout with all components of configuration
	 *         dialog.
	 */
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

		// SPARQL Update Query textArea
		txtQuery = new TextArea();

		txtQuery.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				final String query = txtQuery.getValue().trim();

				Validator validator = new SPARQLUpdateValidator(query);

				if (!validator.isQueryValid()) {

					isQueryValid = false;
					errorMessage = validator.getErrorMessage();

				} else {
					isQueryValid = true;
				}


			}
		});

		txtQuery.addValidator(new com.vaadin.data.Validator() {
			@Override
			public void validate(Object value) throws InvalidValueException {
				if (value.toString().isEmpty()) {
					ex = new EmptyValueException(
							"Update query must be filled");
					throw ex;
				}
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

	/**
	 * Load values from configuration object implementing {@link DPUConfigObject}
	 * interface and configuring DPU into the dialog where the configuration
	 * object may be edited.
	 *
	 * @throws ConfigException Exception not used in current implementation of
	 *                         this method.
	 * @param conf Object holding configuration which is used to initialize
	 *             fields in the configuration dialog.
	 */
	@Override
	public void setConfiguration(SPARQLTransformerConfig conf) throws ConfigException {
		txtQuery.setValue(conf.SPARQL_Update_Query.trim());
	}

	/**
	 * Set values from from dialog where the configuration object may be edited
	 * to configuration object implementing {@link DPUConfigObject} interface and
	 * configuring DPU
	 *
	 * @throws ConfigException Exception which might be thrown when
	 *                         {@link #isQueryValid} is false.
	 * @return conf Object holding configuration which is used in
	 *         {@link #setConfiguration} to initialize fields in the
	 *         configuration dialog.
	 */
	@Override
	public SPARQLTransformerConfig getConfiguration() throws ConfigException {

		//Right SPARQL VALIDATOR - default false
		if (!txtQuery.isValid()) {
			throw new ConfigException(ex.getMessage(), ex);
		} else if (!isQueryValid) {
			throw new SPARQLValidationException(errorMessage);
		} else {

			SPARQLTransformerConfig conf = new SPARQLTransformerConfig();
			conf.SPARQL_Update_Query = txtQuery.getValue().trim();

			return conf;
		}
	}
}
