package cz.cuni.mff.xrg.odcs.frontend.gui.details;

import java.text.DateFormat;
import java.util.Locale;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Window;

import cz.cuni.mff.xrg.odcs.commons.app.execution.log.Log;
import static com.vaadin.server.Sizeable.Unit;

import java.util.Date;

import org.apache.log4j.Level;
import org.springframework.web.util.HtmlUtils;

/**
 * Shows detail of selected log message.
 *
 * @author Bogo
 * @author Petyr
 */
public class LogMessageDetail extends Window {

	private final TextArea fullMessageContent;
	
	private final Label timeContent = new Label();
	
	private final Label levelContent = new Label();
	
	private final Label sourceContent = new Label();
	
	private final DateFormat df =
			DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault());

	/**
	 * Constructor with Record to show.
	 *
	 * @param log
	 */
	public LogMessageDetail(Log log) {
		this.setCaption("Log message detail");
		GridLayout mainLayout = new GridLayout(2, 7);
		mainLayout.setImmediate(true);
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		mainLayout.setSizeFull();

		Label timeLabel = new Label("Time:");
		//At least one component must have fixed width, for expand ratio to work correctly for content column.
		timeLabel.setWidth("60px");
		mainLayout.addComponent(timeLabel, 0, 0);
		mainLayout.addComponent(timeContent, 1, 0);

		Label levelLabel = new Label("Level:");
		mainLayout.addComponent(levelLabel, 0, 2);
		mainLayout.addComponent(levelContent, 1, 2);

		Label sourceLabel = new Label("Source:");
		mainLayout.addComponent(sourceLabel, 0, 3);
		mainLayout.addComponent(sourceContent, 1, 3);

		Label messageLabel = new Label("Message:");
		mainLayout.addComponent(messageLabel, 0, 4);

		fullMessageContent = new TextArea();
		fullMessageContent.setValue("");
		fullMessageContent.setReadOnly(true);
		fullMessageContent.setSizeFull();
		fullMessageContent.setWordwrap(true);
		mainLayout.addComponent(fullMessageContent, 0, 5, 1, 5);
		mainLayout.setComponentAlignment(fullMessageContent, Alignment.TOP_LEFT);

		Button closeButton = new Button("Close", new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				close();
			}
		});
		mainLayout.addComponent(closeButton, 1, 6);
		mainLayout.setComponentAlignment(closeButton, Alignment.MIDDLE_RIGHT);


		mainLayout.setColumnExpandRatio(1, 1.0f);
		mainLayout.setRowExpandRatio(5, 1.0f);

		loadMessage(log);
		
		this.setContent(mainLayout);

	}

	/**
	 * Load new log message to existing detail window.
	 *
	 * @param log Log message to load.
	 */
	public final void loadMessage(Log log) {
		timeContent.setValue(df.format(new Date(log.getTimestamp())));
		levelContent.setValue(Level.toLevel(log.getLogLevel()).toString());
		sourceContent.setValue(log.getSource());
		
		fullMessageContent.setReadOnly(false);
		// set the of main text box
		if (log.getStackTrace() == null) {
			fullMessageContent.setValue(log.getMessage());
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(log.getMessage());
		
			if (log.getStackTrace().isEmpty()) {
				// no stack trace
			} else {
				sb.append("\n\nStack trace:\n");
				// just do replace in stack trace
				final String stackTrace = 
						log.getStackTrace(); //.replace("<", "&lt;").replace("&", "&amp;");
				sb.append(stackTrace);
			}
			fullMessageContent.setValue(sb.toString());
		}
		fullMessageContent.setReadOnly(true);
		
		setContentHeight(this.getHeight(), this.getHeightUnits());
	}

	/**
	 * Resizes content due to resize of whole dialog.
	 *
	 * @param height New height of whole dialog.
	 * @param unit {@link Unit} of height.
	 */
	public void setContentHeight(float height, Sizeable.Unit unit) {
		fullMessageContent.setHeight(height - 230, unit);
	}
}
