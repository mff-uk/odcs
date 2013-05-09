package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

/**
 * Simple query view for querying debug data.
 * @author Bogo
 */
public class QueryView extends CustomComponent {

	public QueryView() {
		VerticalLayout mainLayout = new VerticalLayout();

		HorizontalLayout topLine = new HorizontalLayout();

		NativeSelect graphSelect = new NativeSelect("Graph:");
		graphSelect.addItem("Input Graph");
		graphSelect.addItem("Output Graph");
		topLine.addComponent(graphSelect);

		Button queryButton = new Button("Query");
		queryButton.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				//TODO: QUERY
			}
		});
		topLine.addComponent(queryButton);
		mainLayout.addComponent(topLine);

		TextArea queryText = new TextArea("SPARQL Query:");
		queryText.setWidth("100%");
		queryText.setHeight("200px");
		mainLayout.addComponent(queryText);

		//TODO: Change to table - resolve container issue
		TextArea resultText = new TextArea("Result:");
		resultText.setWidth("100%");
		resultText.setHeight("300px");
		mainLayout.addComponent(resultText);

		//Table resultTable = new Table("Result:");


		setCompositionRoot(mainLayout);
	}


}
