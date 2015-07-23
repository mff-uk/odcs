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
package cz.cuni.mff.xrg.odcs.rdf.validators;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.parser.QueryParserUtil;

import cz.cuni.mff.xrg.odcs.rdf.interfaces.QueryValidator;

/**
 * Class responsible to find out, if sparql update queries are valid or not. It
 * using very often as query in transformer.
 * 
 * @author Jiri Tomes
 */
public class SPARQLUpdateValidator implements QueryValidator {

    private String updateQuery;

    private String message;

    /**
     * Create new instance of {@link SPARQLUpdateValidator} with given SPARQL
     * update query you can validate.
     * 
     * @param updateQuery
     *            SPARQL update query you can validate
     */
    public SPARQLUpdateValidator(String updateQuery) {
        this.updateQuery = updateQuery;
        this.message = "";
    }

    /**
     * Method for detection right syntax of query.
     * 
     * @return true, if query is valid, false otherwise.
     */
    @Override
    public boolean isQueryValid() {
        boolean isValid = true;

        try {
            QueryParserUtil.parseUpdate(QueryLanguage.SPARQL,
                    updateQuery, null);
        } catch (MalformedQueryException e) {
            message = e.getCause().getMessage();
            isValid = false;
        }

        return isValid;
    }

    /**
     * String message describes syntax problem of validation query.
     * 
     * @return empty string, when query is valid.
     */
    @Override
    public String getErrorMessage() {
        return message;
    }
}
