package cz.cuni.xrg.intlib.commons.app.rdf.namespace;

import javax.persistence.*;

/**
 * Entity representing RDF namespace prefix.
 *
 * @author Jan Vojt
 */
@Entity
@Table(name = "rdf_prefix")
public class NamespacePrefix {
	
	/**
	 * Primary key of entity.
	 */
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * Prefix for namespace.
	 */
	@Column(length = 25)
	private String name;
	
	/**
	 * URI represented by prefix.
	 */
	@Column(name="uri", length = 255)
	private String prefixURI;

	/**
	 * Default constructor is required by JPA.
	 */
	public NamespacePrefix() {
	}
	
	/**
	 * Constructs new prefix with given name for given URI.
	 * 
	 * @param name prefix
	 * @param prefixURI URI
	 */
	public NamespacePrefix(String name, String prefixURI) {
		this.name = name;
		this.prefixURI = prefixURI;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrefixURI() {
		return prefixURI;
	}

	public void setPrefixURI(String prefixURI) {
		this.prefixURI = prefixURI;
	}
	
}
