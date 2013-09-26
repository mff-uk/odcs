package virtuoso.eclipselink;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.platform.database.H2Platform;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

/**
 * EclipseLink session customizer is used to slip in custom {@link VirtuosoPlatform}.
 * 
 * <p>
 * When overriding platform in session customizer, we need to specify some dummy
 * value for <code>eclipselink.target-database</code> property in persistence.xml.
 * Specifying a platform will disable eclipselinks auto-detection, which would
 * override our platform spoofed in session customizer.
 * 
 * <p>
 * Session customizer detects whether we are running in a test. If so H2 in-memory
 * database platform is used instead of Virtuoso.
 * 
 *
 * @author Jan Vojt
 */
public class VirtuosoSessionCustomizer implements SessionCustomizer {
	
	/** Name of the system property informing about test run. */
	public static final String TEST_ENVIRONMENT_ATTR = "test-run";

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
					// normal run -> use Virtuoso platform as RDMS
					String platform = VirtuosoPlatform.class.getName();
					session.getLogin().setPlatformClassName(platform);
				}
			}
		};
		
		// Register session event listener
		session.getEventManager().addListener(myEventListener);
	}

}
