/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.xrg.intlib.commons.app.data.rdf;

/**
 * Stub class for representing RDF Triple for browsing in frontend.
 *
 * @author Bogo
 */
public class RDFTriple {

	private int id;

	private String subject;

	private String predicate;

	private String object;

	public RDFTriple(int i, String s, String p, String o) {
		id = i;
		subject = s;
		predicate = p;
		object = o;
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
