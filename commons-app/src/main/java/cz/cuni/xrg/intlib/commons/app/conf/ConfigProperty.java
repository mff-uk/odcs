package cz.cuni.xrg.intlib.commons.app.conf;

/**
 * Recognized configuration properties.
 *
 * @author Jan Vojt
 * @copyright (c) 2013 Jan Vojt
 */
public enum ConfigProperty {
	
	GENERAL_WORKINGDIR("general.workingdir"),

	BACKEND_HOST("backend.host"),
	BACKEND_PORT("backend.port"),
	
	BACKEND_DEFAULTRDF("backend.defaultRdf"),
	
	VIRTUOSO_HOSTNAME("virtuoso.hostname"),
	VIRTUOSO_PORT("virtuoso.port"),
	VIRTUOSO_USER("virtuoso.user"),
	VIRTUOSO_PASSWORD("virtuoso.password"),
	VIRTUOSO_DEFAULT_GRAPH("virtuoso.defautgraph"),
	VIRTUOSO_RETRIES("virtuoso.retries"),
	VIRTUOSO_WAIT("virtuoso.wait"),
	
	MODULE_PATH("module.path"),
	MODULE_FRONT_EXPOSE("module.frontend.expose"),
	MODULE_BACK_EXPOSE("module.backend.expose"),
		
	EMAIL_ENABLED("email.enabled"),
	EMAIL_SMTP_HOST("email.smtp.host"),
	EMAIL_SMTP_PORT("email.smtp.port"),
	EMAIL_SMTP_TTL("email.smtp.ttl"),
	EMAIL_FROM_EMAIL("email.from"),	
	EMAIL_AUTHORIZATION("email.authorization"),
	EMAIL_USERNAME("email.username"),
	EMAIL_PASSWORD("email.password")
	;
	
	private final String property;
	
	private ConfigProperty(final String property) {
		this.property = property;
	}

	@Override
	public String toString() {
		return property;
	}
}
