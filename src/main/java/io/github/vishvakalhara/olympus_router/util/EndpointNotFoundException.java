package io.github.vishvakalhara.olympus_router.util;

import io.github.vishvakalhara.olympus_router.RouterBase;

import javax.servlet.http.HttpServletResponse;

/**
 * @deprecated This exception is no longer used in the routing workflow.
 * Use {@link RouterBase#handleEndpointNotFoundException(HttpServletResponse)} to handle
 * missing endpoint logic directly within the router.
 *
 * This class was originally used to indicate that no matching route was found,
 * but has been replaced for better flexibility and control over HTTP error responses.
 */
@Deprecated
public class EndpointNotFoundException extends Exception {

    /**
     * Constructs an EndpointNotFoundException with the specified detail message.
     *
     * @param err the detail message (which is saved for later retrieval
     *            by the Throwable.getMessage() method)
     */
    public EndpointNotFoundException(String err) {
        super(err);
    }
}
