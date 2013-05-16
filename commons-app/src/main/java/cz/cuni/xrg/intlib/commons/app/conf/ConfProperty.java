package cz.cuni.xrg.intlib.commons.app.conf;

/**
 * Recognized configuration properties.
 *
 * @author Jan Vojt
 * @copyright (c) 2013 Jan Vojt
 */
public enum ConfProperty {
	
	GENERAL_WORKINGDIR("general.workingdir"),

	BACKEND_HOST("backend.host"),
	BACKEND_PORT("backend.port"),
	
	MODULE_PATH("module.path"),
	MODULE_LIBS("module.libs"),
	MODULE_FRONT_EXPOSE("module.frontend.expose"),
	MODULE_BACK_EXPOSE("module.backend.expose"),
	;
	
	private final String property;
	
	private ConfProperty(final String property) {
		this.property = property;
	}

	@Override
	public String toString() {
		return property;
	}
}
