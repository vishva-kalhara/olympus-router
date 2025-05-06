package io.github.vishvakalhara;

import io.github.vishvakalhara.util.HttpMethod;
import io.github.vishvakalhara.util.RouteHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Abstract servlet base class that provides foundational HTTP routing capabilities.
 * Subclasses must implement route registration while inheriting standardized
 * request handling, error responses, and lifecycle management.
 *
 * <p><b>Implementation Note:</b>
 * Requires init parameter "domain" to be configured in web.xml.
 *
 * @author Wishva Kalhara Chandrasekara
 * @see HttpServlet
 */
public abstract class RouterBase extends HttpServlet {

    private final String domain;

    /**
     * Creates a new router instance for the specified domain.
     *
     * @param domain the domain to associate with this router (e.g., "api.example.com")
     */
    public RouterBase(String domain) {
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
    public final RouterBase register(HttpMethod method, String urlPattern, RouteHandler handler) {
        AppRouter.getInstance().register(this.domain, method, urlPattern, handler);
        return this;
    }

    /**
     * Routes an incoming HTTP request to the appropriate handler based on the registered HTTP method and URL pattern.
     * <p>
     * If the request matches a registered route, the associated handler is invoked. Path parameters (e.g., {@code /users/:id})
     * are extracted and injected into the {@link HttpServletRequest} as attributes prefixed with {@code "param_"}.
     * <p>
     * If no matching route is found, the abstract method {@link #handleEndpointNotFoundException(HttpServletResponse)}
     * is invoked to handle the fallback behavior (such as returning a custom 404 error).
     *
     * @param method the HTTP method of the request (e.g., GET, POST)
     * @param req    the HTTP servlet request
     * @param resp   the HTTP servlet response
     * @throws IOException if an I/O error occurs during request handling
     * @see RouteHandler#route(HttpServletRequest, HttpServletResponse)
     */

    public final void route(HttpMethod method, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path == null) {
            handleEndpointNotFoundException(resp);
            return;
        }

        AppRouter.RouteMatchResult result = AppRouter.getInstance().match(domain, method, path);
        if (result == null) {
            handleEndpointNotFoundException(resp);
            return;
        }

        for (Map.Entry<String, String> entry : result.params.entrySet()) {
            req.setAttribute("param_" + entry.getKey(), entry.getValue());
        }
        result.handler.route(req, resp);
    }

    /**
     * Handles HTTP GET requests by routing to registered GET handlers.
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if servlet-specific errors occur
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        this.route(HttpMethod.GET, req, resp);
    }

    /**
     * Handles HTTP POST requests by routing to registered POST handlers.
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if servlet-specific errors occur
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        this.route(HttpMethod.POST, req, resp);
    }

    /**
     * Handles HTTP PUT requests by routing to registered PUT handlers.
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if servlet-specific errors occur
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected final void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        this.route(HttpMethod.PUT, req, resp);
    }

    /**
     * Handles HTTP DELETE requests by routing to registered DELETE handlers.
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @throws ServletException if servlet-specific errors occur
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected final void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        this.route(HttpMethod.DELETE, req, resp);
    }

    /**
     * Handles cases where no matching endpoint is found for the request.
     * <p>
     * Subclasses should implement this method to define custom behavior
     * when a requested route does not exist (e.g., sending a custom 404 response).
     *
     * @param resp the {@link HttpServletResponse} used to write the error response
     * @throws IOException if an I/O error occurs while writing the response
     */
    public abstract void handleEndpointNotFoundException(HttpServletResponse resp) throws IOException;
}
