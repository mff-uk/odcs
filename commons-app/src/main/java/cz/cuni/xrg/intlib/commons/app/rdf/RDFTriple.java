/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.xrg.intlib.commons.app.rdf;

/**
 * Stub class for representing RDF Triple for browsing in frontend.
 *
 * @author Bogo
 * @author Jiri Tomes
 */
public class RDFTriple {

	private int id;

	private String subject;

	private String predicate;

	private String object;

	public RDFTriple(int id, String subject, String predicate, String object) {
		this.id = id;
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @return the predicate
	 */
	public String getPredicate() {
		return predicate;
	}

	/**
	 * @return the object
	 */
	public String getObject() {
		return object;
	}


}
