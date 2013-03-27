package cz.cuni.intlib.commons.app.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="pipeline_model")
public class Pipeline {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	/**
	 * Pipeline name
	 */
	private String name;
	
	/**
	 * Pipeline description of functionality
	 */
	private String description;
	
	/**
	 * Constructor
	 * @param name
	 * @param description
	 */
	public Pipeline(String name, String description) {
		this.name = name;
		this.description = description;
	}

	/**
	 * Constructor for pipeline with empty description
	 * @param name
	 */
	public Pipeline(String name) {
		this(name, "");
	}
	
	/**
	 * Empty constructor is mandatory for Hibernate entities
	 */
	public Pipeline() {}
	
	/**
	 * Getter for ID, which is used in DB as primary key
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}