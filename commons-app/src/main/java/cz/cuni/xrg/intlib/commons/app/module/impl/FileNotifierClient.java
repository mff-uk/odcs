package cz.cuni.xrg.intlib.commons.app.module.impl;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cz.cuni.xrg.intlib.commons.app.dpu.DPURecord;
import cz.cuni.xrg.intlib.commons.app.module.ModuleChangeNotifier;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacadeConfig;

/**
 * Implement {@link ModuleChangeNotifier} by using shared file system. To notify
 * backend we simply create new file with given name.
 * 
 * To receive the notifications use {@link FileNotifierServer}.
 * 
 * @author Petyr
 * 
 */
class FileNotifierClient implements ModuleChangeNotifier {	
	
	private static final Logger LOG = LoggerFactory.getLogger(FileNotifierClient.class);
	
	/**
	 * Used to get information about modules.
	 */
	@Autowired
	private ModuleFacadeConfig moduleConfig;

	public void updated(DPURecord dpu) {
		// notification file name
		final String dpuFolder = moduleConfig.getDpuFolder();
		final String notificationFileName =  
				dpu.getJarPath() + FileNotifierServer.NOTIFICATION_EXT;
		
		File notificationFile = new File(dpuFolder, notificationFileName);
		if (notificationFile.exists()) {
			// backend do not use the DPU from last update			
		} else {
			try {
				notificationFile.createNewFile();
			} catch(IOException | SecurityException e) {
				LOG.warn("Failed to create notificaiton file.", e);
			}			
		}
		
	}

}
