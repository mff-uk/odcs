package cz.cuni.mff.xrg.odcs.transformer.SPARQL;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.EmptyValueException;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.*;

import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.configuration.DPUConfigObject;
import cz.cuni.mff.xrg.odcs.commons.module.dialog.BaseConfigDialog;
import cz.cuni.mff.xrg.odcs.rdf.enums.SPARQLQueryType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.SPARQLValidationException;
import cz.cuni.mff.xrg.odcs.rdf.validators.SPARQLQueryValidator;
import cz.cuni.mff.xrg.odcs.rdf.validators.SPARQLUpdateValidator;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.QueryValidator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Configuration dialog for DPU SPARQL Transformer.
 *
 * @author Jiri Tomes
 * @author Maria Kukhar
 */
public class SPARQLTransformerDialog extends BaseConfigDialog<SPARQLTransformerConfig> {

	private static final long serialVersionUID = 1L;

	private GridLayout mainLayout;

//	private TextArea txtQuery;

	private Label labelUpQuer;
	
	private GridLayout gridLayoutQuery;
	
	private TextArea textFieldQuery;
	
	private Button buttonQueryRem;
	
	private Button buttonQueryAdd;
	
	private InvalidValueException ex;

	/**
	 * SPARQL VALIDATOR - default false.
	 */
	private boolean isQueryValid = false;

	/**
	 * If query is construct or other update query.
	 */
	private boolean isConstructQuery = false;

	private String validationErrorMessage = "";
	
	private void inicialize() {
		ex = new Validator.InvalidValueException("Valid");
	}

	/**
	 * Constructor.
	 */
	public SPARQLTransformerDialog() {
		super(SPARQLTransformerConfig.class);
		inicialize();
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	/**
	 * Load values from configuration object implementing
	 * {@link DPUConfigObject} interface and configuring DPU into the dialog
	 * where the configuration object may be edited.
	 *
	 * @throws ConfigException Exception not used in current implementation of
	 *                         this method.
	 * @param conf Object holding configuration which is used to initialize
	 *             fields in the configuration dialog.
	 */
	@Override
	public void setConfiguration(SPARQLTransformerConfig conf) throws ConfigException {

//		txtQuery.setValue(conf.getSPARQLUpdateQuery());
		
		try {
			sparqlQuery = conf.getSPARQLUpdateQuery();
			if (sparqlQuery == null) {
				sparqlQuery = new LinkedList<>();
			}
		} catch (Exception e) {
			sparqlQuery = new LinkedList<>();
		}
		refreshSparqlQueryData();
		isConstructQuery = conf.isConstructType();
	}

	/**
	 * Set values from from dialog where the configuration object may be edited
	 * to configuration object implementing {@link DPUConfigObject} interface
	 * and configuring DPU
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
		if (!areSparqlQueriesValid()) {
			InvalidValueException ex = new EmptyValueException(
					"SPARQL query must be filled");
			throw new ConfigException(ex.getMessage(), ex);
		} else if (!isQueryValid) {
			throw new SPARQLValidationException(validationErrorMessage);
		} else {
			
			saveEditedTexts();
			
//			String query = txtQuery.getValue().trim();
			SPARQLTransformerConfig conf = null;
			String query = null;
			boolean ex=false;
			for(String q: getSparqlQueries()){
				query = q.trim();
				if (isConstructQuery && !hasValidMoreGraphsForContruct(query)) {
					ex=true;
					throw new SPARQLValidationException(validationErrorMessage);
				} 
			}
			if(!ex){
				
			boolean isConstructType = isConstructQuery;
			conf = new SPARQLTransformerConfig(getSparqlQueries(),
						isConstructType);
			}	

			return conf;	
		}
	}

	private boolean isPossibleDPUName(String extractedName) {

		boolean result = false;

		for (String dpuName : SPARQLTransformer.DPUNames) {
			if (dpuName.equals(extractedName)) {
				result = true;
				break;
			}
		}
		return result;

	}

	private boolean hasValidMoreGraphsForContruct(String contructQuery) {

		PlaceholdersHelper helper = new PlaceholdersHelper();
		List<String> extractedNames = helper.getExtractedDPUNames(contructQuery);

		if (extractedNames.isEmpty()) {
			return true;
		} else {

			for (String nextExtractedName : extractedNames) {
				if (!isPossibleDPUName(nextExtractedName)) {
					validationErrorMessage = String.format(
							"This INPUT name \"%s\" used in query doesnt exists. "
							+ "Use name defined in mapping for this DPU !!!",
							nextExtractedName);
					return false;
				}
			}
			return true;
		}
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

/*		// SPARQL Update Query textArea
		txtQuery = new TextArea();

		txtQuery.addValidator(new com.vaadin.data.Validator() {
			@Override
			public void validate(Object value) throws InvalidValueException {
				final String query = txtQuery.getValue().trim();

				if (query.isEmpty()) {

					throw new EmptyValueException("SPARQL query must be filled");
				}

				QueryValidator updateValidator = new SPARQLUpdateValidator(query);
				SPARQLQueryValidator constructValidator = new SPARQLQueryValidator(
						query, SPARQLQueryType.CONSTRUCT);

				boolean isConstructValid = constructValidator.isQueryValid();
				boolean isUpdateValid = updateValidator.isQueryValid();

				if (isConstructValid) {
					isQueryValid = true;
					isConstructQuery = true;
					return;
				} else {
					//if is construct query, but no valid
					if (constructValidator.hasSameType()) {
						isConstructQuery = true;
						isQueryValid = false;
						validationErrorMessage = constructValidator
								.getErrorMessage();
						return;
					} else {
						isConstructQuery = false;
					}
				}

				if (isUpdateValid) {
					isQueryValid = true;
				} else {
					isQueryValid = false;
					validationErrorMessage = updateValidator
							.getErrorMessage();
				}
			}
		});

		txtQuery.setNullRepresentation("");
		txtQuery.setImmediate(true);
		txtQuery.setWidth("100%");
		txtQuery.setHeight("211px");
		txtQuery.setInputPrompt(
				"PREFIX br:<http://purl.org/business-register#>\nMODIFY\nDELETE { ?s pc:contact ?o}\nINSERT { ?s br:contact ?o}\nWHERE {\n\t     ?s a gr:BusinessEntity .\n\t      ?s pc:contact ?o\n}");
*/
		//SPARQL Update Query component
		initializeSparqlQueryList();

		mainLayout.addComponent(gridLayoutQuery, 1, 0);

		
//		mainLayout.addComponent(txtQuery, 1, 0);
		mainLayout.setColumnExpandRatio(0, 0.00001f);
		mainLayout.setColumnExpandRatio(1, 0.99999f);

		return mainLayout;
	}
	
	/**
	 * List<String> that contains SPARQL Update Query.
	 */
	private List<String> sparqlQuery = initializeGridData();

	/**
	 * Initializes data of the SPARQL Update Query component
	 */
	private static List<String> initializeGridData() {
		List<String> result = new LinkedList<>();
		result.add("");

		return result;

	}

	private List<String> getSparqlQueries() {
		List<String> result = new LinkedList<>();

		for (String nextQuery : sparqlQuery) {
			String query = nextQuery.trim();
			if (!query.isEmpty()) {
				result.add(query);
			}
		}
		return result;
	}

	/**
	 * Add new data to SPARQL Update Query component
	 *
	 * @param newData. Query that will be added
	 */
	private void addDataToGridData(String newData) {
		sparqlQuery.add(newData.trim());
	}

	/**
	 * Remove data from SPARQL Update Query component. Only if component contain more
	 * then 1 row.
	 *
	 * @param row Data that will be removed.
	 */
	private void removeDataFromGridData(Integer row) {
		int index = row;
		if (sparqlQuery.size() > 1) {
			sparqlQuery.remove(index);
		}
	}

	private List<TextArea> listedEditText = null;

	/**
	 * Save edited texts in the Named Graph component
	 */
	private void saveEditedTexts() {
		sparqlQuery.clear();
		for (TextArea editText : listedEditText) {
			sparqlQuery.add(editText.getValue().trim());
		}
	}

	/**
	 *
	 * @return if all SPARQL Update Queries are valid or not.
	 */
	private boolean areSparqlQueriesValid() {
		for (TextArea next : listedEditText) {
			if (!next.isValid()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Builds SPARQL Update Query component which consists of textareas for SPARQL query
	 * and buttons for add and remove this textareas. Used in
	 * {@link #initializeSparqlQueryList()} and also in adding and removing textareas
	 * for component refresh
	 */
	private void refreshSparqlQueryData() {
		gridLayoutQuery.removeAllComponents();
		int row = 0;
		listedEditText = new ArrayList<>();
		if (sparqlQuery.size() < 1) {
			sparqlQuery.add("");
		}
		gridLayoutQuery.setRows(sparqlQuery.size() + 1);
		for (String item : sparqlQuery) {
			textFieldQuery = new TextArea();
			listedEditText.add(textFieldQuery);
			//text area for the query
			textFieldQuery.setNullRepresentation("");
			textFieldQuery.setImmediate(true);
			textFieldQuery.setWidth("100%");
			textFieldQuery.setHeight("211px");
			textFieldQuery.setData(row);
			textFieldQuery.setValue(item.trim());
			textFieldQuery.setInputPrompt("PREFIX br:<http://purl.org/business-register#>\nMODIFY\nDELETE { ?s pc:contact ?o}\nINSERT { ?s br:contact ?o}\nWHERE {\n\t     ?s a gr:BusinessEntity .\n\t      ?s pc:contact ?o\n}");
			textFieldQuery.addValidator(new Validator() {
				private static final long serialVersionUID = 1L;

				@Override
				public void validate(Object value) throws InvalidValueException {
					final String query = textFieldQuery.getValue().trim();

					if (query.isEmpty()) {
						
						ex = new Validator.InvalidValueException(
								"SPARQL query must be filled");
						throw ex;

					}

					QueryValidator updateValidator = new SPARQLUpdateValidator(query);
					SPARQLQueryValidator constructValidator = new SPARQLQueryValidator(
							query, SPARQLQueryType.CONSTRUCT);

					boolean isConstructValid = constructValidator.isQueryValid();
					boolean isUpdateValid = updateValidator.isQueryValid();

					if (isConstructValid) {
						isQueryValid = true;
						isConstructQuery = true;
						return;
					} else {
						//if is construct query, but no valid
						if (constructValidator.hasSameType()) {
							isConstructQuery = true;
							isQueryValid = false;
							validationErrorMessage = constructValidator
									.getErrorMessage();
							return;
						} else {
							isConstructQuery = false;
						}
					}

					if (isUpdateValid) {
						isQueryValid = true;
					} else {
						isQueryValid = false;
						validationErrorMessage = updateValidator
								.getErrorMessage();
					}
				}
			});

			//remove button
			buttonQueryRem = new Button();
			buttonQueryRem.setWidth("55px");
			buttonQueryRem.setCaption("-");
			buttonQueryRem.setData(row);
			buttonQueryRem.addClickListener(new Button.ClickListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(Button.ClickEvent event) {
					saveEditedTexts();
					Button senderButton = event.getButton();
					Integer row = (Integer) senderButton.getData();
					removeDataFromGridData(row);
					refreshSparqlQueryData();
				}
			});
			gridLayoutQuery.addComponent(textFieldQuery, 0, row);
			gridLayoutQuery.addComponent(buttonQueryRem, 1, row);
			gridLayoutQuery.setComponentAlignment(buttonQueryRem,
					Alignment.TOP_RIGHT);
			row++;
		}

		//add button
		buttonQueryAdd = new Button();
		buttonQueryAdd.setCaption("+");
		buttonQueryAdd.setImmediate(true);
		buttonQueryAdd.setWidth("55px");
		buttonQueryAdd.setHeight("-1px");
		buttonQueryAdd.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
				saveEditedTexts();
				addDataToGridData("");
				refreshSparqlQueryData();
			}
		});
		gridLayoutQuery.addComponent(buttonQueryAdd, 0, row);
	}

	/**
	 * Initializes Named Graph component. Calls from
	 * {@link #buildVerticalLayoutCore()}
	 */
	private void initializeSparqlQueryList() {

		gridLayoutQuery = new GridLayout();
		gridLayoutQuery.setImmediate(true);
		gridLayoutQuery.setWidth("100%");
		gridLayoutQuery.setHeight("100%");
		gridLayoutQuery.setMargin(false);
		gridLayoutQuery.setColumns(2);
		gridLayoutQuery.setColumnExpandRatio(0, 0.95f);
		gridLayoutQuery.setColumnExpandRatio(1, 0.05f);

		refreshSparqlQueryData();
	}


	
}
