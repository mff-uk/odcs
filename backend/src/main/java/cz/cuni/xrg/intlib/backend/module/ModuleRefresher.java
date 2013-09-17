package cz.cuni.xrg.intlib.backend.module;

import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;


import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.app.module.event.DPURefreshEvent;

/**
 *
 * @author Petyr
 *
 */
public class ModuleRefresher implements ApplicationListener<DPURefreshEvent> {

	@Autowired
	private ModuleFacade moduleFacade;
		
	private final static Logger LOG = LoggerFactory.getLogger(ModuleRefresher.class);

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
