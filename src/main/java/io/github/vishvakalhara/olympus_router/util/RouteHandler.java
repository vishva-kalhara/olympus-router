package io.github.vishvakalhara.olympus_router.util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A functional interface representing an HTTP route handler.
 * Implementations of this interface process incoming HTTP requests and generate responses.
 * This is typically used to define endpoint behavior in a web server or API framework.
 *
 * <p>Example usage:
 * <pre>{@code
 * RouteHandler helloHandler = (req, resp) -> {
 *     resp.setContentType("text/plain");
 *     resp.getWriter().write("Hello, World!");
 * };
 * }</pre>
 *
 * @author Wishva Kalhara Chandrasekara
 * @see HttpServletRequest
 * @see HttpServletResponse
 */
@FunctionalInterface
public interface RouteHandler {

    /**
     * Handles an HTTP request and generates an appropriate response.
     * Implementations should define the logic for processing the request,
     * such as reading input, performing operations, and writing output.
     *
     * @param req  the incoming HTTP request
     * @param resp the HTTP response to be sent back
     * @throws IOException if an I/O error occurs during request handling
     * @return true if the request should continue to the next handler; false to stop.
     */
    boolean route(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException;
}
