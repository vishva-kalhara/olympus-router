package io.github.vishvakalhara.olympus_router;

import io.github.vishvakalhara.olympus_router.util.HttpMethod;
import io.github.vishvakalhara.olympus_router.util.RouteHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

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

    private final List<RouteHandler> commonMiddlewares;

    /**
     * Creates a new router instance for the specified domain.
     *
     * @param domain the domain to associate with this router (e.g., "api.example.com")
     */
    @Deprecated
    public RouterBase(String domain) {
        this.domain = domain;
        commonMiddlewares = new ArrayList<>();
    }

    /**
     * Creates a new router instance for the specified domain.
     */
    public RouterBase() {
        this.domain = String.valueOf(System.identityHashCode(this));
        commonMiddlewares = new ArrayList<>();
    }

    /**
     * Registers a new route with the router using method chaining.
     *
     * @param method      the HTTP method (GET, POST, etc.)
     * @param urlPattern  the URL pattern with parameters (e.g., "/users/:id")
     * @param middlewares the function to handle matching requests
     * @return this router instance for method chaining
     * @see AppRouter#register(String, HttpMethod, String, List)
     *
     * <p><b>Example:</b>
     * <pre>{@code
     * new Router("users")
     *     .register(HttpMethod.GET, "/users", listUsersHandler)
     *     .register(HttpMethod.POST, "/users", createUserHandler);
     * }</pre>
     */
    public final RouterBase register(HttpMethod method, String urlPattern, RouteHandler... middlewares) {
        List<RouteHandler> combined = new ArrayList<>(commonMiddlewares);
        combined.addAll(Arrays.asList(middlewares));
        AppRouter.getInstance().register(this.domain, method, urlPattern, combined);
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
     * @see RouteHandler#route(HttpServletRequest, HttpServletResponse)
     */
    public final void route(HttpMethod method, HttpServletRequest req, HttpServletResponse resp) {
        try {
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

            for (RouteHandler middleware : result.middlewares) {
                if (middleware == null || !middleware.route(req, resp)) {
                    return;
                }
            }
        } catch (IOException e) {
            handleException(e, resp);
        }
    }

    /**
     * Registers one or more global middleware handlers to be executed before any route-specific handlers.
     * <p>
     * Middleware added using this method will run for every incoming request handled by this router,
     * regardless of the HTTP method or path. This is useful for tasks such as authentication, logging,
     * or request preprocessing that should apply across all routes.
     *
     * <p><b>Example:</b>
     * <pre>{@code
     * router.use(authMiddleware, loggingMiddleware);
     * }</pre>
     *
     * @param middlewares one or more {@link RouteHandler} instances to be added as global middleware
     * @return this {@link RouterBase} instance for method chaining
     */
    public final RouterBase use(RouteHandler... middlewares) {
        Collections.addAll(commonMiddlewares, middlewares);
        return this;
    }

    /**
     * Handles incoming HTTP requests by delegating to the appropriate route handler.
     * Catches any exceptions and delegates to {@link #handleException(Exception, HttpServletResponse)}.
     *
     * @param method the HTTP method of the request
     * @param req    the HTTP servlet request
     * @param resp   the HTTP servlet response
     */
    private void handleRoute(HttpMethod method, HttpServletRequest req, HttpServletResponse resp) {
        try {
            this.route(method, req, resp);
        } catch (Exception e) {
            handleException(e, resp);
        }
    }

    /**
     * Handles HTTP GET requests by routing to registered GET handlers.
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     */
    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) {

        this.handleRoute(HttpMethod.GET, req, resp);
    }

    /**
     * Handles HTTP POST requests by routing to registered POST handlers.
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     */
    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp) {

        this.handleRoute(HttpMethod.POST, req, resp);
    }

    /**
     * Handles HTTP PUT requests by routing to registered PUT handlers.
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     */
    @Override
    protected final void doPut(HttpServletRequest req, HttpServletResponse resp) {

        this.handleRoute(HttpMethod.PUT, req, resp);
    }

    /**
     * Handles HTTP DELETE requests by routing to registered DELETE handlers.
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     */
    @Override
    protected final void doDelete(HttpServletRequest req, HttpServletResponse resp) {

        this.handleRoute(HttpMethod.DELETE, req, resp);
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

    /**
     * Handles all the exceptions.
     * <p>
     * Subclasses should implement this method to define custom behavior
     * to handle thrown exceptions.
     *
     * @param e    the {@link Exception} used to write the error response
     * @param resp the {@link HttpServletResponse} used to write the error response
     */
    public abstract void handleException(Exception e, HttpServletResponse resp);
}
