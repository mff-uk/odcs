/*******************************************************************************
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package cz.cuni.mff.xrg.odcs.rdf.help;

/**
 * Class for representing RDF Triple for browsing in frontend.
 * 
 * @author Bogo
 * @author Jiri Tomes
 */
public class RDFTriple {

    private int id;

    private String subject;

    private String predicate;

    private String object;

    /**
     * Constructor with complete information about triple.
     * 
     * @param id
     *            Id of triple for indexing in container.
     * @param subject
     *            {@link String} with subject of triple.
     * @param predicate
     *            {@link String} with predicate of triple.
     * @param object
     *            {@link String} with object of triple.
     */
    public RDFTriple(int id, String subject, String predicate, String object) {
        this.id = id;
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    /**
     * Returns ID for indexing in container.
     * 
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns string value of subject.
     * 
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Returns string value of predicate.
     * 
     * @return the predicate
     */
    public String getPredicate() {
        return predicate;
    }

    /**
     * Returns string value of object.
     * 
     * @return the object
     */
    public String getObject() {
        return object;
    }
}
