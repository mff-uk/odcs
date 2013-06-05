/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import cz.cuni.xrg.intlib.commons.app.execution.Record;

/**
 *
 * @author Bogo
 */
public class RecordDetail extends CustomComponent {



	public RecordDetail(Record record) {
		GridLayout mainLayout = new GridLayout(2, 20);
		mainLayout.setSpacing(true);
		mainLayout.setWidth("600px");
		mainLayout.setHeight("400px");

		Label pipelineExecutionLabel = new Label("Pipeline execution:");
		mainLayout.addComponent(pipelineExecutionLabel, 0, 0);
		Label pipelineExecutionContent = new Label(String.format("%d", record.getExecution().getId()));
		mainLayout.addComponent(pipelineExecutionContent, 1, 0);

		Label timeLabel = new Label("Time:");
		mainLayout.addComponent(timeLabel, 0, 1);
		Label timeContent = new Label(record.getTime().toString());
		mainLayout.addComponent(timeContent, 1, 1);

		Label instanceLabel = new Label("Type:");
		mainLayout.addComponent(instanceLabel, 0, 2);
		Label instanceContent = new Label(record.getType().toString());
		mainLayout.addComponent(instanceContent, 1, 2);

		Label shortMessageLabel = new Label("Short message:");
		mainLayout.addComponent(shortMessageLabel, 0, 3);
		Label shortMessageContent = new Label(record.getShortMessage());
		mainLayout.addComponent(shortMessageContent, 1, 3);

		TextArea fullMessageContent = new TextArea("Full Message:", record.getFullMessage());
		fullMessageContent.setSizeFull();
		mainLayout.addComponent(fullMessageContent, 0, 4, 1, 19);

		setCompositionRoot(mainLayout);

	}

}
