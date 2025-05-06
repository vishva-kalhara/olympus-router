package io.github.vishvakalhara;

import io.github.vishvakalhara.util.EndpointNotFoundException;
import io.github.vishvakalhara.util.HttpMethod;
import io.github.vishvakalhara.util.RouteHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * An abstract base class for building domain-specific HTTP routers in a servlet-based application.
 * <p>
 * This class simplifies route registration and request dispatching by wrapping the global {@link AppRouter}
 * with domain-aware functionality. It supports URL pattern matching with dynamic path parameters (e.g., {@code "/users/:id"})
 * and injects those parameters into the {@link HttpServletRequest} attributes using the prefix {@code "param_"}.
 * <p>
 * Extend this class to define your own router with custom behavior, including centralized exception handling.
 *
 * <h3>Key Features:</h3>
 * <ul>
 *     <li>Domain-scoped routing (per subdomain or logical context)</li>
 *     <li>Fluent method chaining for route registration</li>
 *     <li>Dynamic path parameters injected as request attributes</li>
 *     <li>Supports all HTTP methods defined by {@link HttpMethod}</li>
 *     <li>Designed for extendability and custom exception handling</li>
 * </ul>
 *
 * <h3>Example Usage:</h3>
 * <pre>{@code
 * public class UserRouter extends RouterBase {
 *
 *     private final UserController controller = new UserController();
 *
 *     public UserRouter() {
 *         super("users");
 *         this.register(HttpMethod.GET, "/all", controller::getAllUsers)
 *             .register(HttpMethod.GET, "/:id", controller::getUserById);
 *     }
 * }
 * }</pre>
 *
 * @author
 *   Wishva Kalhara Chandrasekara
 *
 * @see AppRouter
 * @see RouteHandler
 * @see HttpMethod
 * @see EndpointNotFoundException
 */

public abstract class RouterBase {

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
