package gui;

import module.Config;

import com.vaadin.ui.*;

import cz.cuni.xrg.intlib.commons.configuration.*;

/**
 * Configuration dialog.
 * @author Petyr
 *
 */
public class ConfigDialog extends CustomComponent {

	private static final long serialVersionUID = 1L;
	
	/**
	 * TODO Implement your own configuration component. You can use vaadin visual editor if you like.
	 * Just remember don't use vaddin classes the ere not located directly in package com.vaadi.ui; 
	 */
	
	private GridLayout mainLayout;

	private TextField txtUrl;
	
	private TextField txtLogin;
	
	private TextField txtPassword;
	
	private TextArea txtQuery;
		
	public ConfigDialog() {
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}
	
	/**
	 * Return current configuration from dialog. Can return null, if 
	 * current configuration is invalid.
	 * @return current configuration or null
	 */
	public Configuration getConfiguration() { 
		Configuration config = new Configuration();
		/**
		 * TODO Gather data from you dialog and store them into configuration. You can use 
		 * 	enum Config to make sure that you don't miss spell the ids of values.
		 * 	Also remember that you can return null in case of invalid configuration in dialog.
		 */
		
		config.setValue(Config.Url.name(), txtUrl.getValue());
		config.setValue(Config.Login.name(), txtLogin.getValue());
		config.setValue(Config.Password.name(), txtPassword.getValue());
		config.setValue(Config.Query.name(), txtQuery.getValue());

		return config;
	}
	
	/**
	 * Load values from configuration into dialog.
	 * @throws ConfigurationException
	 * @param conf
	 */
	public void setConfiguration(Configuration conf) {
		/**
		 * TODO Load configuration from conf into dialog components. You can use 
		 * 	enum Config to make sure that you don't miss spell the ids of values.
		 *  The ConfigurationException can be thrown in case of invalid configuration.
		 */
		
		try
		{
			txtUrl.setValue( (String) conf.getValue(Config.Url.name()));
			txtLogin.setValue( (String) conf.getValue(Config.Login.name()));
			txtPassword.setValue( (String) conf.getValue(Config.Password.name()));
			txtQuery.setValue( (String) conf.getValue(Config.Query.name()));
		} 
		catch(Exception ex) {
			// throw setting exception
			throw new ConfigurationException();
		}
	}
	
	private GridLayout buildMainLayout() {
		/**
		 * TODO Build your component here.
		 */
		
		
		// common part: create layout
		mainLayout = new GridLayout(2, 4);
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		
		// top-level component properties
		setWidth("600px");
		setHeight("300px");
				
		txtUrl = new TextField();
		txtUrl.setWidth("450px");
		txtUrl.setHeight("-1px");
		mainLayout.addComponent(txtUrl, 1, 0);
		mainLayout.addComponent(new Label("Url:"), 0, 0);
		
		txtLogin = new TextField();
		txtLogin.setWidth("450px");
		txtLogin.setHeight("-1px");
		mainLayout.addComponent(txtLogin, 1, 1);
		mainLayout.addComponent(new Label("Login:"), 0, 1);
		
		txtPassword = new TextField();
		txtPassword.setWidth("450px");
		txtPassword.setHeight("-1px");
		mainLayout.addComponent(txtPassword, 1, 2);
		mainLayout.addComponent(new Label("Password:"), 0, 2);
		
		txtQuery = new TextArea();
		txtQuery.setWidth("450px");
		txtQuery.setHeight("300px");
		mainLayout.addComponent(txtQuery, 1, 3);
		mainLayout.addComponent(new Label("Query:"), 0, 3);		
		
		return mainLayout;
	}

}
