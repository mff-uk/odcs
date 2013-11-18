package cz.cuni.mff.xrg.odcs.commons.app.rdf.namespace;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Facade for managing persisted entities of {@link NamespacePrefix}.
 * 
 * @author Jan Vojt
 */
@Transactional(readOnly = true)
public class NamespacePrefixFacade {
	
	private static final Logger LOG = LoggerFactory.getLogger(NamespacePrefixFacade.class);
	
	@Autowired
	private DbNamespacePrefix prefixDao;
	
	/**
	 * Namespace prefix factory.
	 * 
	 * @param name
	 * @param URI
	 * @return 
	 */
	public NamespacePrefix createPrefix(String name, String URI) {
		return new NamespacePrefix(name, URI);
	}
	
	/**
	 * Fetch all RDF namespace prefixes defined in application.
	 * 
	 * @return 
	 */
	public List<NamespacePrefix> getAllPrefixes() {
		return prefixDao.getAllPrefixes();
	}
	
	/**
	 * Fetch a single namespace RDF prefix given by ID.
	 * 
	 * @param id
	 * @return 
	 */
	public NamespacePrefix getPrefix(long id) {
		return prefixDao.getInstance(id);
	}
	
	/**
	 * Find prefix with given name in persistent storage.
	 * 
	 * @param name
	 * @return 
	 */
	public NamespacePrefix getPrefixByName(String name) {
		return prefixDao.getByName(name);
	}
	
	/**
	 * Persists given RDF namespace prefix. If it is persisted already, all changes
	 * performed on object are updated.
	 * 
	 * @param prefix namespace prefix to persist or update
	 */
	@Transactional
	public void save(NamespacePrefix prefix) {
		prefixDao.save(prefix);
	}
	
	/**
	 * Deletes RDF namespace prefix from persistent storage.
	 * 
	 * @param prefix 
	 */
	@Transactional
	public void delete(NamespacePrefix prefix) {
		prefixDao.delete(prefix);
	}
	
}
