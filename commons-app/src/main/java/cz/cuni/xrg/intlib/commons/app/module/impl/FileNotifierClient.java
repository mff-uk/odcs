package cz.cuni.xrg.intlib.commons.app.module.impl;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.xrg.intlib.commons.app.module.ModuleChangeNotifier;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacadeConfig;

/**
 * Implement {@link ModuleChangeNotifier} by using shared file system. To notify
 * backend we simply create notification file in DPU's directory.
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

	@Autowired(required=false)
	private FileNotifierServer server;
	
	public void updated(DPUTemplateRecord dpu) {
		// notification file name
		final String dpuSubDir = dpu.getJarDirectory();
		final String dpuDir = moduleConfig.getDpuFolder() + 
				File.separator + dpuSubDir;
		
		File notificationFile = new File(dpuDir, 
				dpu.getJarName() + 	FileNotifierServer.NOTIFICATION_EXT);
		
		if (notificationFile.exists()) {
			// server listen on create .. so we need to delete
			// old instance first, so we can create new one
			notificationFile.delete();
		}
		
		if (server == null) {
			// no server is in our instance
		} else {
			// we say server to ignore the notification
			server.ignore(dpuSubDir);
		}
		
		try {
			notificationFile.createNewFile();
		} catch(IOException | SecurityException e) {
			LOG.warn("Failed to create notificaiton file.", e);
		}
		
	}

}
