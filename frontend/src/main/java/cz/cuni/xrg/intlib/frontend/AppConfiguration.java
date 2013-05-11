package cz.cuni.xrg.intlib.frontend;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import cz.cuni.xrg.intlib.commons.app.module.ModuleFacadeConfiguration;

/**
 * Contains application configuration.
 * 
 * @author Petyr
 */
public class AppConfiguration {

	/**
	 * ModuleFacade configuration. 
	 */
	private ModuleFacadeConfiguration moduleConfiguration = new ModuleFacadeConfiguration();
	
	/**
	 * Backend instance IP address.
	 */
	private String backendAddress = "127.0.0.1";
	
	/**
	 * Port for communicating with backend.
	 */
	private Integer backendPort = 5010;
	
	public AppConfiguration() {
		// init moduleConfiguration		
		moduleConfiguration.setPackagesToExpose(
			// commons
            "cz.cuni.xrg.intlib.commons"
            + ",cz.cuni.xrg.intlib.commons.configuration"
            + ",cz.cuni.xrg.intlib.commons.data"
            + ",cz.cuni.xrg.intlib.commons.data.rdf"
            + ",cz.cuni.xrg.intlib.commons.event"
            + ",cz.cuni.xrg.intlib.commons.extractor"
            + ",cz.cuni.xrg.intlib.commons.loader"
            + ",cz.cuni.xrg.intlib.commons.message"
            + ",cz.cuni.xrg.intlib.commons.transformer"
            // commons-web
            + ",cz.cuni.xrg.intlib.commons.web"
            // vaadin    
			+ ",com.vaadin.ui"
			+ ",com.vaadin.data"
			+ ",com.vaadin.data.util"
			+ ",com.vaadin.data.util.converter"
			+ ",com.vaadin.shared.ui.combobox"
			+ ",com.vaadin.server"
			// OpenRdf
			+ ",org.openrdf.rio"
		);
		
	}
	
	/**
	 * Load configuration from given properties file.
	 * @param fileName
	 * @throws IOException 
	 * @throws RuntimeException
	 */
	public void Load(String fileName) throws IOException, RuntimeException {
		FileInputStream stream;
		
		stream = new FileInputStream(fileName);
		Load(stream);
		stream.close();
	}
	
	/**
	 * Load configuration from given stream.
	 * @param fileName
	 * @throws IOException
	 * @throws RuntimeException 
	 */
	public void Load(FileInputStream stream) throws IOException, RuntimeException {
		Properties prop = new Properties();
		
		prop.loadFromXML(stream);
		moduleConfiguration.setDpuFolder( prop.getProperty("dpuDirectory") );
		backendAddress = prop.getProperty("backendAddress");
		try {
			backendPort = Integer.parseInt( prop.getProperty("backendPort") );
		} catch (NumberFormatException e) {
			throw new RuntimeException("Can't parse port number.", e);
		}
	}
	
	public ModuleFacadeConfiguration getModuleFacadeConfiguration() {
		return this.moduleConfiguration;
	}
	
	public String getBackendAddress() {
		return this.backendAddress;
	}
	
	public Integer getBackendPort() {
		return this.backendPort;
	}
	
}
