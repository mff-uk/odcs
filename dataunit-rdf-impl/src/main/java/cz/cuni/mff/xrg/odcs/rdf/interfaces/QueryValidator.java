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
package cz.cuni.mff.xrg.odcs.rdf.interfaces;

/**
 * It is responsible for right validation of queries.
 * 
 * @author Jiri Tomes
 */
public interface QueryValidator {

    /**
     * Method for detection right syntax of query.
     * 
     * @return true, if query is valid, false otherwise.
     */
    public boolean isQueryValid();

    /**
     * String message describes syntax problem of validation query.
     * 
     * @return empty string, when query is valid.
     */
    public String getErrorMessage();
}
