package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.Validator;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Edge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.dialogs.ConfirmDialog;

/**
 * Window showing Edge detail. Currently only corresponding DataUnit can be named.
 *
 * @author Bogo
 */
public class EdgeDetail extends Window {

	private final static Logger LOG = LoggerFactory.getLogger(EdgeDetail.class);

	private final Edge edge;

	private TextField edgeName;


	/**
	 * Basic constructor, takes {@link Edge} which detail should be showed.
	 *
	 * @param edge {@link Edge} which detail will be showed.
	 */
	public EdgeDetail(Edge e) {

		this.setResizable(false);
		this.setModal(true);
		this.edge = e;
		this.setCaption("Edge detail");

		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setStyleName("dpuDetailMainLayout");
		mainLayout.setMargin(true);

		GridLayout edgeSettingsLayout = new GridLayout(2, 1);
		edgeSettingsLayout.setSpacing(true);

		Label nameLabel = new Label("Data Unit Name:");
		nameLabel.setWidth("-1px");
		nameLabel.setHeight("-1px");
		edgeSettingsLayout.addComponent(nameLabel, 0, 0);

		edgeName = new TextField();
		edgeName.setImmediate(false);
		edgeName.setWidth("200px");
		edgeName.setHeight("-1px");
		if(edge.getDataUnitName() != null) {
			edgeName.setValue(edge.getDataUnitName());
		}
		edgeName.setInputPrompt("Insert DataUnit name");
		edgeName.setRequired(true);
		edgeName.setRequiredError("Edge name must be filled! For unnaming of DataUnit use Remove Named Data Unit button!");
//		edgeName.addValueChangeListener(new Property.ValueChangeListener() {
//			@Override
//			public void valueChange(Property.ValueChangeEvent event) {
//				setCaption(edgeName.getValue());
//			}
//		});
		edgeSettingsLayout.addComponent(edgeName, 1, 0);

		edgeSettingsLayout.setMargin(new MarginInfo(false, false, true,
				false));
		mainLayout.addComponent(edgeSettingsLayout);


		HorizontalLayout buttonBar = new HorizontalLayout();
		buttonBar.setStyleName("dpuDetailButtonBar");
		buttonBar.setMargin(new MarginInfo(true, false, false, false));

		Button saveAndCommitButton = new Button("Save",
				new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				if (save()) {
					close();
				}
			}
		});
		buttonBar.addComponent(saveAndCommitButton);

		Button cancelButton = new Button("Cancel", new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				close();
			}
		});
		buttonBar.addComponent(cancelButton);

		Label placeFiller = new Label(" ");
		buttonBar.addComponent(placeFiller);

		Button removeNamingButton = new Button("Remove Named Data Unit",
				new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				ConfirmDialog.show(UI.getCurrent(),
						"Really remove named data unit?",
						new ConfirmDialog.Listener() {
					@Override
					public void onClose(ConfirmDialog cd) {
						if (cd.isConfirmed()) {
							edge.setDataUnitName(null);
							close();
						}
					}
				});
			}
		});
		buttonBar.addComponent(removeNamingButton);

		mainLayout.addComponent(buttonBar);

		this.setContent(mainLayout);
		setSizeUndefined();
	}

	/**
	 * Saves configuration of Edge which was set in detail dialog.
	 *
	 * @return True if save was successful, false otherwise.
	 */
	protected boolean save() {
			if (!validate()) {
				return false;
			}
			edge.setDataUnitName(edgeName.getValue());
			return true;
	}

	
	private boolean validate() {
		try {
			edgeName.validate();
		} catch (Validator.InvalidValueException e) {
			Notification.show("Error saving Edge configuration. Reason:", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			return false;
		}
		return true;
	}
}
