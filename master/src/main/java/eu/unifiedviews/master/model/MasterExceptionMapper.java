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
package eu.unifiedviews.master.model;

import eu.unifiedviews.master.i18n.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class MasterExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = LoggerFactory.getLogger(MasterExceptionMapper.class);

    /**
     * Map an exception to a {@link Response}. Returning
     * {@code null} results in a {@link Response.Status#NO_CONTENT}
     * response. Throwing a runtime exception results in a
     * {@link Response.Status#INTERNAL_SERVER_ERROR} response.
     *
     * @param throwable the throwable to map to a response.
     * @return a response mapped from the supplied exception.
     */
    @Override
    public Response toResponse(Throwable throwable) {
        LOG.error("Exception raised:", throwable);

        ErrorResponse response = new ErrorResponse();

        // uncomment this part, if you want to send whole stacktrace
        //StringWriter errorStackTrace = new StringWriter();
        //throwable.printStackTrace(new PrintWriter(errorStackTrace));
        //response.setTechnicalMessage(errorStackTrace.toString());

        // send just original throwable message
        response.setErrorMessage(Messages.getString("general.exception"));
        response.setTechnicalMessage(throwable.getMessage());

        int statusCode = determineHttpStatus(throwable);
        return Response.status(statusCode)
                .entity(response)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private int determineHttpStatus(Throwable ex) {
        if (ex instanceof WebApplicationException) {
            return ((WebApplicationException) ex).getResponse().getStatus();
        } else {
            return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        }
    }
}
