package cz.cuni.xrg.intlib.frontend.gui.components;

import java.text.DateFormat;
import java.util.Locale;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Window;

import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecord;

/**
 * Shows detail of selected event record.
 *
 * @author Bogo
 */
public class RecordDetail extends Window {
	
	TextArea fullMessageContent;
	private Label pipelineExecutionContent;
	private Label timeContent;
	private Label instanceContent;
	private Label shortMessageContent;
	private String formattedDate;

	/**
	 * Constructor with Record to show.
	 *
	 * @param record Record which detail should be showed.
	 */
	public RecordDetail(MessageRecord record) {
		this.setCaption("Record detail");
		GridLayout mainLayout = new GridLayout(2, 7);
		mainLayout.setImmediate(true);
		mainLayout.setSpacing(true);
		mainLayout.setSizeFull();
		
		Label pipelineExecutionLabel = new Label("Pipeline execution:");
		pipelineExecutionLabel.setWidth(120, Unit.PIXELS);
		mainLayout.addComponent(pipelineExecutionLabel, 0, 0);
		pipelineExecutionContent = new Label(String.format("%d", record.getExecution().getId()));
		pipelineExecutionContent.setWidth(100, Unit.PIXELS);
		mainLayout.addComponent(pipelineExecutionContent, 1, 0);
		
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM,  Locale.getDefault());
		formattedDate = df.format(record.getTime());
		
		Label timeLabel = new Label("Time:");
		mainLayout.addComponent(timeLabel, 0, 1);
		timeContent = new Label(formattedDate);
		mainLayout.addComponent(timeContent, 1, 1);
		
		Label instanceLabel = new Label("Type:");
		mainLayout.addComponent(instanceLabel, 0, 2);
		instanceContent = new Label(record.getType().toString());
		mainLayout.addComponent(instanceContent, 1, 2);
		
		Label shortMessageLabel = new Label("Short message:");
		mainLayout.addComponent(shortMessageLabel, 0, 3);
		shortMessageContent = new Label(record.getShortMessage());
		mainLayout.addComponent(shortMessageContent, 1, 3);
		
		Label messageLabel = new Label("Message:");
		mainLayout.addComponent(messageLabel, 0, 4);
		
		fullMessageContent = new TextArea();
                fullMessageContent.setValue(record.getFullMessage());
		fullMessageContent.setReadOnly(true);
		fullMessageContent.setSizeFull();
		mainLayout.addComponent(fullMessageContent, 0, 5, 1, 5);
		
		mainLayout.setColumnExpandRatio(1, 1.0f);
		mainLayout.setRowExpandRatio(5, 1.0f);
		
		Button closeButton = new Button("Close", new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				close();
			}
		});
		mainLayout.addComponent(closeButton, 1, 6);
		mainLayout.setComponentAlignment(closeButton, Alignment.MIDDLE_RIGHT);
		
		this.setContent(mainLayout);
		
	}

	/**
	 * Resizes content due to resize of whole dialog.
	 *
	 * @param height New height of whole dialog.
	 * @param unit
	 * @{link Unit} of height.
	 */
	void setContentHeight(float height, Unit unit) {
		fullMessageContent.setHeight(height - 250, unit);
	}
	
	/**
	 * Load new record detail to existing detail window.
	 *
	 * @param record Record to load.
	 */
	void loadMessage(MessageRecord record) {
		pipelineExecutionContent.setValue(String.format("%d", record.getExecution().getId()));
		timeContent.setValue(formattedDate);
		instanceContent.setValue(record.getType().toString());
		shortMessageContent.setValue(record.getShortMessage());
                fullMessageContent.setReadOnly(false);
		fullMessageContent.setValue(record.getFullMessage());
                fullMessageContent.setReadOnly(true);
	}
}
