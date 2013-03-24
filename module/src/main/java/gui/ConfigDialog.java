package gui;

import com.vaadin.ui.*;

public class ConfigDialog extends CustomComponent {

	private static final long serialVersionUID = 1L;
	
	private AbsoluteLayout mainLayout;
	private TextField txtValue;
	private Button btnSave;
		
	public ConfigDialog(String value) {
		buildMainLayout();
		setCompositionRoot(mainLayout);
		// set initial value		
		this.txtValue.setValue(value);
		// 
		this.btnSave.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
				// show some dialog ?
				Notification.show("something happend", "message button clicked ..",
						com.vaadin.ui.Notification.Type.TRAY_NOTIFICATION);
			}
		});
	}
	
	public String getSetting() { 
		return this.txtValue.getValue();
	}
	
	public void setSetting(String value) {
		this.txtValue.setValue(value);
	}
	
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		
		// top-level component properties
		setWidth("600px");
		setHeight("300px");
		
		// button_1
		btnSave = new Button();
		btnSave.setCaption("show message..");
		btnSave.setImmediate(false);
		btnSave.setWidth("-1px");
		btnSave.setHeight("-1px");
		mainLayout.addComponent(btnSave, "top:60.0px;left:224.0px;");
		
		// txtValue
		txtValue = new TextField();
		txtValue.setImmediate(false);
		txtValue.setWidth("257px");
		txtValue.setHeight("-1px");
		mainLayout.addComponent(txtValue, "top:20.0px;left:23.0px;");
		
		return mainLayout;
	}

}
