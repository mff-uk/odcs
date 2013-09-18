package cz.cuni.xrg.intlib.backend.module;

import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private final static Logger LOG = LoggerFactory.getLogger(ModuleRefresher.class);
	
	@Autowired
	private ModuleFacade moduleFacade;
	
	@Override
	public void onApplicationEvent(DPURefreshEvent event) {
		final String relativePath = event.getRelativePath();
		// just call update on ModuleFacade
		try {			
			moduleFacade.update(relativePath);
		} catch (BundleException e) {
			LOG.error("Faield to update bundle: {}", relativePath, e);
		}
	}
		
}
