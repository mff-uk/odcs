package cz.cuni.xrg.intlib.commons.app.dpu;

import cz.cuni.xrg.intlib.commons.configuration.Configuration;


/**
 * Configuration setting for DPU component on the canvas.
 *
 * @author Jiri Tomes
 */
public class InstanceConfiguration extends TemplateConfiguration {

	private Configuration configuration;

	public InstanceConfiguration(Configuration config) {
		configuration = config;
	}
}
