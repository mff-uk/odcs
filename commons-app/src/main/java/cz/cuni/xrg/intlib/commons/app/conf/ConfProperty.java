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
	MODULE_EXPOSE("module.expose"),
	MODULE_LIBS("module.libs"),
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
