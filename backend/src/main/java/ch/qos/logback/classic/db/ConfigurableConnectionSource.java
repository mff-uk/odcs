package ch.qos.logback.classic.db;

import ch.qos.logback.core.db.DriverManagerConnectionSource;
import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import virtuoso.ConfigurableDataSource;

/**
 * Connection source for logback. Database login is loaded from {@link AppConfig}.
 *
 * @author Jan Vojt
 */
public class ConfigurableConnectionSource extends DriverManagerConnectionSource {
	
	/**
	 * Initialize database login properties.
	 */
	@Override
	public void start() {
		// This is ugly, but Spring's autowiring is not available yet. Spring
		// initializes logging (and thus logback) before application context is
		// initialized. This could be circumvented by setting up a listener for
		// Spring context initialization and replace logging appender afterwards.
		// However, this is too complicated, so we use this simple but ugly
		// manual initialization of dataSource instead. :(
		ConfigurableDataSource dataSource = new ConfigurableDataSource(new AppConfig());

		setDriverClass(ConfigurableDataSource.DRIVER_CLASS_NAME);
		setUrl(dataSource.getUrl());
		setUser(dataSource.getUsername());
		setPassword(dataSource.getPassword());
		super.start();
	}	
}
