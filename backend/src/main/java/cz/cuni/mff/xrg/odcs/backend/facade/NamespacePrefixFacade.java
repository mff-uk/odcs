package cz.cuni.mff.xrg.odcs.backend.facade;

import cz.cuni.mff.xrg.odcs.commons.app.rdf.namespace.NamespacePrefix;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Facade for managing RDF prefixes, which tolerates database crashes. This facade
 * is specially altered for servicing backend, where we do not want to trash
 * all progress of unfinished pipeline runs just because of a short database
 * outage.
 *
 * <p>
 * TODO The concept of crash-proof facades could be solved nicer and with less
 *		code using AOP.
 * 
 * @author Jan Vojt
 */
public class NamespacePrefixFacade extends cz.cuni.mff.xrg.odcs.commons.app.rdf.namespace.NamespacePrefixFacade {
	
	/**
	 * Handler taking care of DB outages.
	 */
	@Autowired
	private ErrorHandler handler;

	@Override
	public List<NamespacePrefix> getAllPrefixes() {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getAllPrefixes();
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public NamespacePrefix getPrefix(long id) {
		int attempts = 0;
		while (true) try {
			attempts++;
		return super.getPrefix(id);
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public NamespacePrefix getPrefixByName(String name) {
		int attempts = 0;
		while (true) try {
			attempts++;
		return super.getPrefixByName(name);
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public void save(NamespacePrefix prefix) {
		int attempts = 0;
		while (true) try {
			attempts++;
			super.save(prefix);
			return;
		} catch (IllegalArgumentException ex) {
			// given user is a removed entity
			throw ex;
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public void delete(NamespacePrefix prefix) {
		int attempts = 0;
		while (true) try {
			attempts++;
			super.delete(prefix);
			return;
		} catch (IllegalArgumentException ex) {
			// given user is not persisted
			throw ex;
		} catch (RuntimeException ex) {
			handler.handle(attempts, ex);
		}
	}
}
