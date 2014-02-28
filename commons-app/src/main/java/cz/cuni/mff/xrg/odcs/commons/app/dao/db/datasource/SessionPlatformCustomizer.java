package cz.cuni.mff.xrg.odcs.commons.app.dao.db.datasource;

import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.conf.InvalidConfigPropertyException;
import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.platform.database.H2Platform;
import org.eclipse.persistence.platform.database.MySQLPlatform;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import virtuoso.eclipselink.VirtuosoPlatform;

/**
 * EclipseLink session customizer is used to slip in custom database platform.
 * 
 * <p>
 * When overriding platform in session customizer, we need to specify some dummy
 * value for {@code eclipselink.target-database} property in persistence.xml.
 * Specifying a platform will disable EclipseLink's auto-detection, which would
 * override our platform spoofed in session customizer.
 * 
 * <p>
 * Session customizer detects whether we are running in a test. If so H2 in-memory
 * database platform is used instead of Virtuoso or MySQL.
 * 
 * <p>
 * The customizer is really needed only for Virtuoso, as EclipseLink does not
 * support this platform.
 * 
 * @see VirtuosoPlatform
 *
 * @author Jan Vojt
 */
@Configurable
public class SessionPlatformCustomizer implements SessionCustomizer {
	
	/** Name of the system property informing about test run. */
	public static final String TEST_ENVIRONMENT_ATTR = "test-run";
	
	@Autowired
	private AppConfig config;

	/**
	 * Customizes EclipseLink session, so that we can use {@link VirtuosoPlatform}.
	 * 
	 * @param session
	 * @throws Exception 
	 */
	@Override
	public void customize(Session session) throws Exception {

		SessionEventAdapter myEventListener = new SessionEventAdapter() {
			// Listen for preLogin event
			@Override
			public void preLogin(SessionEvent event) {

				Session session = event.getSession();
				if (Boolean.TRUE.toString().equals(System.getProperty(TEST_ENVIRONMENT_ATTR))) {
					// we are running in a test -> use H2 platform as RDBMS
					session.getLogin().setPlatformClassName(H2Platform.class.getName());
				} else {
					// normal run -> determine platform from configuration
					String dbEngine = config
							.getSubConfiguration(ConfigProperty.RDBMS)
							.getString(ConfigProperty.DATABASE_PLATFORM);

					switch (dbEngine) {
						case DataSourceFactory.MYSQL_VALUE :
							session.getLogin().setPlatformClassName(MySQLPlatform.class.getName());
							break;
						case DataSourceFactory.VIRTUOSO_VALUE :
							session.getLogin().setPlatformClassName(VirtuosoPlatform.class.getName());
							break;
						default :
							throw new InvalidConfigPropertyException(ConfigProperty.DATABASE_PLATFORM);
					}
				}
			}
		};
		
		// Register session event listener
		session.getEventManager().addListener(myEventListener);
	}

}
