package cz.cuni.xrg.intlib.commons.app.module;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.app.module.event.DPURefreshEvent;

/**
 * Class react on {@link DPURefreshEvent} and secure update of the 
 * given DPUs.
 *
 * @author Petyr
 *
 */
class ModuleRefresher implements ApplicationListener<DPURefreshEvent> {

	@Autowired
	private ModuleFacade moduleFacade;
	
	@Override
	public void onApplicationEvent(DPURefreshEvent event) {
		final String relativeDir = event.getDirectoryName();
		// just uninstall the current version
		// the new version will be loaded on demand
		moduleFacade.uninstallDir(relativeDir);
	}
		
}
