package io.github.vishvakalhara.util;

/**
 * Thrown to indicate that a requested endpoint could not be found.
 * This exception typically occurs when attempting to access an API endpoint
 * that doesn't exist or isn't available.
 *
 * @author Wishva Kalhara Chandrasekara
 */
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
