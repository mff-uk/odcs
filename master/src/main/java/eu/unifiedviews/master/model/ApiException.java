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
package eu.unifiedviews.master.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Exception of Master API.
 */
public class ApiException extends WebApplicationException {
    private static final long serialVersionUID = -5793273224388941908L;

    private static final Logger LOG = LoggerFactory.getLogger(ApiException.class);

    /**
     * When thrown, this exception send response to client with error details.
     *
     * @param status HTTP status code to give to error.
     * @param errorMessage Localized error message. Can be shown in GUI.
     * @param technicalMessage Technical error message. Served for dev purposes.
     */
    public ApiException(Status status, String errorMessage, String technicalMessage) {
        super(Response.status(status)
                .entity(new ErrorResponse(errorMessage, technicalMessage)).type(MediaType.APPLICATION_JSON_TYPE).build());
        LOG.error("API exception raised: status: {}, errorMessage: {}, technicalMessage: {}", status, errorMessage, technicalMessage, this);
    }
}
