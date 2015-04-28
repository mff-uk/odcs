package eu.unifiedviews.master.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class ApiException extends WebApplicationException {
    private static final long serialVersionUID = -5793273224388941908L;

    private static final Logger LOG = LoggerFactory.getLogger(ApiException.class);

    public ApiException(Status status, String message) {
        super(Response.status(status)
                .entity(new ErrorResponse(message)).type(MediaType.APPLICATION_JSON_TYPE).build());
        LOG.debug("API exception raised: status: {}, message: {}", status, message);
    }
}
