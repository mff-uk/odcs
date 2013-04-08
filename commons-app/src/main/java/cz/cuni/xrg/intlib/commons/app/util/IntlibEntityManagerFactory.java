package cz.cuni.xrg.intlib.commons.app.util;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public final class IntlibEntityManagerFactory {
	
	/**
	 * Persistence unit name to use for DB connection.
	 * Needs to be properly configured in persistence.xml under this name.
	 */
	public static final String PERSISTENCE_UNIT = "intlib";
	
	/**
	 * Entity Manager factory for application.
	 * @return
	 */
	public static EntityManager create() {
		return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT)
			.createEntityManager();
	}

}
