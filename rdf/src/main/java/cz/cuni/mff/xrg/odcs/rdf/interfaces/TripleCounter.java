/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.xrg.odcs.rdf.interfaces;

/**
 * Interface provides information of extracted triples.
 *
 * @author Jiri Tomes
 */
public interface TripleCounter {

	/**
	 * Return count of extracted triples.
	 *
	 * @return count of extracted triples.
	 */
	public long getTripleCount();

	/**
	 * Return true if there is no triples, false otherwise.
	 *
	 * @return true if there is no triples, false otherwise.
	 */
	public boolean isEmpty();

	/**
	 * Set count of extracted triples to 0.
	 */
	public void reset();
}
