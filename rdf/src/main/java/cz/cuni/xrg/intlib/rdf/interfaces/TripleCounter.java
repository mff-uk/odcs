/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.xrg.intlib.rdf.interfaces;

/**
 * Interface provides information of extracted triples.
 *
 * @author Jiri Tomes
 */
public interface TripleCounter {

	/**
	 *
	 * @return count of extracted triples.
	 */
	public long getTripleCount();

	/**
	 *
	 * @return true if there is no triples, false otherwise.
	 */
	public boolean isEmpty();

	/**
	 * Set count of extracted triples to 0.
	 */
	public void reset();
}
