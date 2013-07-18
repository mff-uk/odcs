package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Window;
import cz.cuni.xrg.intlib.commons.app.execution.LogMessage;

/**
 * Shows detail of selected log message.
 *
 * @author Bogo
 */
public class LogMessageDetail extends Window {

	Label fullMessageContent;
	private Label timeContent;
	private Label threadContent;
	private Label levelContent;
	private Label sourceContent;

	/**
	 * Constructor with Record to show.
	 *
	 * @param record Record which detail should be showed.
	 */
	public LogMessageDetail(LogMessage log) {
		this.setCaption("Log message detail");
		GridLayout mainLayout = new GridLayout(2, 7);
		mainLayout.setImmediate(true);
		mainLayout.setSpacing(true);
		mainLayout.setSizeFull();

		Label timeLabel = new Label("Time:");
		mainLayout.addComponent(timeLabel, 0, 0);
		timeContent = new Label(log.getDate().toString());
		mainLayout.addComponent(timeContent, 1, 0);

		Label threadLabel = new Label("Thread:");
		threadLabel.setWidth(120, Sizeable.Unit.PIXELS);
		mainLayout.addComponent(threadLabel, 0, 1);
		threadContent = new Label(log.getThread());
		threadContent.setWidth(100, Sizeable.Unit.PIXELS);
		mainLayout.addComponent(threadContent, 1, 1);

		Label levelLabel = new Label("Level:");
		mainLayout.addComponent(levelLabel, 0, 2);
		levelContent = new Label(log.getLevel().toString());
		mainLayout.addComponent(levelContent, 1, 2);

		Label sourceLabel = new Label("Source:");
		mainLayout.addComponent(sourceLabel, 0, 3);
		sourceContent = new Label(log.getSource());
		mainLayout.addComponent(sourceContent, 1, 3);
		
		Label messageLabel = new Label("Message:");
		mainLayout.addComponent(messageLabel, 0, 4);

		fullMessageContent = new Label(log.getMessage());
		fullMessageContent.setSizeFull();
		mainLayout.addComponent(fullMessageContent, 0, 5, 1, 5);

		Button closeButton = new Button("Close", new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				close();
			}
		});
		mainLayout.addComponent(closeButton, 1, 6);
		mainLayout.setComponentAlignment(closeButton, Alignment.MIDDLE_RIGHT);

		mainLayout.setColumnExpandRatio(1, 1.0f);
		mainLayout.setRowExpandRatio(4, 1.0f);

		this.setContent(mainLayout);

	}

	/**
	 * Resizes content due to resize of whole dialog.
	 *
	 * @param height New height of whole dialog.
	 * @param unit
	 * @{link Unit} of height.
	 */
	void setContentHeight(float height, Sizeable.Unit unit) {
		fullMessageContent.setHeight(height - 210, unit);
	}

	/**
	 * Load new log message to existing detail window.
	 *
	 * @param log Log message to load.
	 */
	void loadMessage(LogMessage log) {
		timeContent.setValue(log.getDate().toString());
		threadContent.setValue(log.getThread());
		levelContent.setValue(log.getLevel().toString());
		sourceContent.setValue(log.getSource());
		fullMessageContent.setValue(log.getMessage());
	}
}
