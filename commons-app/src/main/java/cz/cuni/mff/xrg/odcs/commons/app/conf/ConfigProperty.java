package cz.cuni.mff.xrg.odcs.commons.app.conf;

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
	
	// namespaces for virtuoso configuration
	VIRTUOSO_RDBMS("virtuoso.rdbms"),
	VIRTUOSO_RDF("virtuoso.rdf"),
	
	VIRTUOSO_HOSTNAME("hostname"),
	VIRTUOSO_PORT("port"),
	VIRTUOSO_USER("user"),
	VIRTUOSO_PASSWORD("password"),
	VIRTUOSO_CHARSET("charset"),
	VIRTUOSO_RETRIES("retries"),
	VIRTUOSO_WAIT("wait"),
	
	MODULE_PATH("module.path"),
	MODULE_FRONT_EXPOSE("module.frontend.expose"),
	MODULE_BACK_EXPOSE("module.backend.expose"),
		
	EMAIL_ENABLED("email.enabled"),
	EMAIL_SMTP_HOST("email.smtp.host"),
	EMAIL_SMTP_PORT("email.smtp.port"),
	EMAIL_SMTP_TLS("email.smtp.tls"),
	EMAIL_FROM_EMAIL("email.from"),	
	EMAIL_AUTHORIZATION("email.authorization"),
	EMAIL_USERNAME("email.username"),
	EMAIL_PASSWORD("email.password"),
	EMAIL_ADMIN("email.admin")
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
