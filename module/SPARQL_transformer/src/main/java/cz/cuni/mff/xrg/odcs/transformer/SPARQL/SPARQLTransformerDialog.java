package cz.cuni.mff.xrg.odcs.transformer.SPARQL;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.*;
import com.vaadin.ui.TabSheet.Tab;

import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.configuration.DPUConfigObject;
import cz.cuni.mff.xrg.odcs.commons.module.dialog.BaseConfigDialog;
import cz.cuni.mff.xrg.odcs.rdf.enums.SPARQLQueryType;
import cz.cuni.mff.xrg.odcs.rdf.validators.SPARQLQueryValidator;
import cz.cuni.mff.xrg.odcs.rdf.validators.SPARQLUpdateValidator;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.QueryValidator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Configuration dialog for DPU SPARQL Transformer.
 *
 * @authod Petr Škoda
 */
public class SPARQLTransformerDialog extends BaseConfigDialog<SPARQLTransformerConfig> {
	
	private enum QueryType {
		INVALID, 
		CONSTRUCT, 
		UPDATE
	};
	
	private Accordion accordion;

	private Button btnDelete;
		
	private final LinkedList<TextArea> queries = new LinkedList<>();

	/**
	 * Is valid only after isValid is called on all components in queries.
	 */
	private final HashMap<TextArea, QueryType> queryTypes = new HashMap<>();
	
	public SPARQLTransformerDialog() {
		super(SPARQLTransformerConfig.class);
		init();
	}

	private void init() {
		this.setSizeFull();
		
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		mainLayout.setSpacing(true);
		
		HorizontalLayout topLineLayout = new HorizontalLayout();
		topLineLayout.setSizeUndefined();
		topLineLayout.setSpacing(true);		
		
		Button btnAddQuery = new Button();
		btnAddQuery.setCaption("Add query tab");
		btnAddQuery.setSizeUndefined();		
		btnAddQuery.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(Button.ClickEvent event) {
				addGraph("CONSTRUCT { ?s ?p ?o } WHERE {?s ?p ?o }");
			}
		});		
		topLineLayout.addComponent(btnAddQuery);
		
		btnDelete = new Button("Delete current");
		btnDelete.setEnabled(false);
		btnDelete.setSizeUndefined();	
		btnDelete.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(Button.ClickEvent event) {
				if (accordion.getSelectedTab() == null) {
					return;
				}
					
				final Tab tab = accordion.getTab(accordion.getSelectedTab());
				final int index = accordion.getTabPosition(tab);
				
				TextArea txtQuery = queries.get(index);
				queries.remove(txtQuery);
				queryTypes.remove(txtQuery);
				accordion.removeTab(tab);
							
				btnDelete.setEnabled(!queries.isEmpty());
			}
		});
		
		topLineLayout.addComponent(btnDelete);
		
		mainLayout.addComponent(topLineLayout);
		mainLayout.setExpandRatio(topLineLayout, 0);		
		
		accordion = new Accordion();
		accordion.setSizeFull();
		mainLayout.addComponent(accordion);
		mainLayout.setExpandRatio(accordion, 1);
				
		setCompositionRoot(mainLayout);
	}
	
	private void addGraph(String query) {
		VerticalLayout subLayout = new VerticalLayout();
		subLayout.setSizeFull();
		subLayout.setMargin(true);
		
		final TextArea txtQuery = new TextArea();
		txtQuery.setSizeFull();
		txtQuery.setValue(query);
		txtQuery.setSizeFull();
		
		subLayout.addComponent(txtQuery);
		
		// add to main component list
		this.queries.add(txtQuery);
		this.queryTypes.put(txtQuery, QueryType.INVALID);
		
		final Tab tab = this.accordion.addTab(subLayout, "Query");
		
		txtQuery.addValidator(new Validator() {

			@Override
			public void validate(Object value) throws InvalidValueException {
				final String query = value.toString();
				
				if (query.isEmpty()) {
					throw new InvalidValueException(
							"SPARQL query is empty it must be filled");
				}				
				
				QueryValidator updateValidator = 
						new SPARQLUpdateValidator(query);
				SPARQLQueryValidator constructValidator = 
						new SPARQLQueryValidator(query, SPARQLQueryType.CONSTRUCT);				

				// also store type in case of sucessful validation
				if (constructValidator.isQueryValid()) {
					queryTypes.put(txtQuery, QueryType.CONSTRUCT);
					return;
				}

				if (updateValidator.isQueryValid()) {
					queryTypes.put(txtQuery, QueryType.UPDATE);
					return;
				}
				
				queryTypes.put(txtQuery, QueryType.INVALID);

				// return message based on query type
				if (constructValidator.hasSameType()) {
					throw new InvalidValueException(
							constructValidator.getErrorMessage());
				} else {
					throw new InvalidValueException(
							updateValidator.getErrorMessage());
				}				
			}
		});
		
		accordion.setSelectedTab(tab);
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
		queries.clear();
		queryTypes.clear();
		accordion.removeAllComponents();
		
		for (SPARQLQueryPair pair : conf.getQueryPairs()) {
			addGraph(pair.getSPARQLQuery());
		}
		
		btnDelete.setEnabled(!conf.getQueryPairs().isEmpty());
	}

	/**
	 * Set values from from dialog where the configuration object may be edited
	 * to configuration object implementing {@link DPUConfigObject} interface
	 * and configuring DPU
	 *
	 * @throws ConfigException Exception which might be thrown when any of
	 *                         SPARQL queries are invalid.
	 * @return conf Object holding configuration which is used in
	 *         {@link #setConfiguration} to initialize fields in the
	 *         configuration dialog.
	 */
	@Override
	public SPARQLTransformerConfig getConfiguration() throws ConfigException {

		SPARQLTransformerConfig conf = new SPARQLTransformerConfig();
		List<SPARQLQueryPair> queryPairs = conf.getQueryPairs();
		
		for (int i = 0 ; i < queries.size(); i++) {
			TextArea txtQuery = queries.get(i);
			if (!txtQuery.isValid()) {
				throw new ConfigException("All queries must be valid!");
			}
			// add to conf
			final boolean isConstruct = queryTypes.get(txtQuery) == QueryType.CONSTRUCT;
			queryPairs.add(new SPARQLQueryPair(txtQuery.getValue(), isConstruct));
		}
		
		return conf;
	}

}
