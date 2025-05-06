package io.github.vishvakalhara;

import io.github.vishvakalhara.util.EndpointNotFoundException;
import io.github.vishvakalhara.util.HttpMethod;
import io.github.vishvakalhara.util.RouteHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * A domain-specific HTTP router that simplifies route registration and request handling.
 * Wraps the global {@link AppRouter} with domain-specific routing capabilities.
 * Supports method chaining for route registration.
 *
 * <p><b>Example Usage:</b>
 * <pre>{@code
 * Router apiRouter = new Router("api.example.com");
 * apiRouter.register(HttpMethod.GET, "/users/:id", (req, resp) -> {
 *     String userId = req.getAttribute("param_id"); // Extract route parameter
 *     // Handle request...
 * });
 * }</pre>
 *
 * @author Wishva Kalhara Chandrasekara
 * @see AppRouter
 * @see EndpointNotFoundException
 */
public class Router {

    private final String domain;

    /**
     * Creates a new router instance for the specified domain.
     *
     * @param domain the domain to associate with this router (e.g., "api.example.com")
     */
    public Router(String domain) {
        this.domain = domain;
    }

    /**
     * Registers a new route with the router using method chaining.
     *
     * @param method     the HTTP method (GET, POST, etc.)
     * @param urlPattern the URL pattern with parameters (e.g., "/users/:id")
     * @param handler    the function to handle matching requests
     * @return this router instance for method chaining
     * @see AppRouter#register(String, HttpMethod, String, RouteHandler)
     *
     * <p><b>Example:</b>
     * <pre>{@code
     * new Router("api.example.com")
     *     .register(HttpMethod.GET, "/users", listUsersHandler)
     *     .register(HttpMethod.POST, "/users", createUserHandler);
     * }</pre>
     */
    public Router register(HttpMethod method, String urlPattern, RouteHandler handler) {
        AppRouter.getInstance().register(this.domain, method, urlPattern, handler);
        return this;
    }

    /**
     * Routes an incoming HTTP request to the appropriate handler.
     * Extracts path parameters and injects them into the request as attributes
     * with "param_" prefixes (e.g., ":id" becomes "param_id").
     *
     * @param method the HTTP method of the request
     * @param req    the HTTP request object
     * @param resp   the HTTP response object
     * @throws EndpointNotFoundException if no matching route is found
     * @throws IOException               if an I/O error occurs during handling
     * @see RouteHandler#route(HttpServletRequest, HttpServletResponse)
     */
    public void route(HttpMethod method, HttpServletRequest req, HttpServletResponse resp) throws IOException, EndpointNotFoundException {
        String path = req.getPathInfo();
        if (path == null) {
            throw new EndpointNotFoundException("Endpoint not found!");
        }

        AppRouter.RouteMatchResult result = AppRouter.getInstance().match(domain, method, path);
        if (result == null) {
            throw new EndpointNotFoundException("Endpoint not found!");
        }

        for (Map.Entry<String, String> entry : result.params.entrySet()) {
            req.setAttribute("param_" + entry.getKey(), entry.getValue());
        }
        result.handler.route(req, resp);
    }
}
