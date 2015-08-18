/**
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
 */
package cz.cuni.mff.xrg.odcs.rdf.exceptions;

/**
 * Exception is thrown when RDF data insert part for loading data to the SPARQL
 * endpoint have some invalid RDF triples.
 * 
 * @author Jiri Tomes
 */
public class InsertPartException extends RDFException {

    /**
     * Create a new instance of {@link InsertPartException} without detail
     * message.
     */
    public InsertPartException() {
        super();
    }

    /**
     * Create new instance of {@link InsertPartException} with specific message.
     * 
     * @param message
     *            String value of described message
     */
    public InsertPartException(String message) {
        super(message);
    }

    /**
     * Create new instance of {@link InsertPartException} with cause of throwing
     * this exception.
     * 
     * @param cause
     *            Cause of throwing exception
     */
    public InsertPartException(Throwable cause) {
        super(cause);
    }

    /**
     * Create new instance of {@link InsertPartException} with a specific
     * message and cause of throwing this exception.
     * 
     * @param message
     *            String value of described message
     * @param cause
     *            Cause of throwing exception
     */
    public InsertPartException(String message, Throwable cause) {
        super(message, cause);
    }
}
