package cz.cuni.xrg.intlib.commons.app.util;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import cz.cuni.xrg.intlib.commons.app.InMemoryEntityManager;

/**
 * EntityManager factory.
 * @author Jan Vojt <jan@vojt.net>
 * TODO remove this and use Spring autowiring instead
 */
public final class IntlibEntityManagerFactory {
	
	/**
	 * Persistence unit name to use for DB connection.
	 * Needs to be properly configured in persistence.xml under this name.
	 */
	private static final String PERSISTENCE_UNIT = "intlib";
	
	private static EntityManager em;
	
	private static EntityManager imem;
	
	/**
	 * Entity Manager factory for application.
	 * @return
	 */
	public static EntityManager getEm() {
		if (em == null) {
			em = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT)
					.createEntityManager();
		}
		return em;
	}
	
	/**
	 * In-memory EntityManager factory.
	 * @return
	 */
	public static EntityManager getImem() {
		if (imem == null) {
			imem = new InMemoryEntityManager();
		}
		return imem;
	}

}
