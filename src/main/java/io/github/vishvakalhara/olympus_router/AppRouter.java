package io.github.vishvakalhara.olympus_router;

import io.github.vishvakalhara.olympus_router.util.HttpMethod;
import io.github.vishvakalhara.olympus_router.util.RouteDefinition;
import io.github.vishvakalhara.olympus_router.util.RouteHandler;
import io.github.vishvakalhara.olympus_router.util.RouteParams;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * A router that manages HTTP route definitions and matches incoming requests to their handlers.
 * This class follows the singleton pattern to ensure a single routing table across the application.
 *
 * <p>Example usage:
 * <pre>{@code
 * AppRouter router = AppRouter.getInstance();
 * router.register("api.example.com", HttpMethod.GET, "/users/:id", (req, resp) -> {
 *     String id = req.getPathParameter("id");
 *     // Handle request...
 * });
 * }</pre>
 *
 * @author Wishva Kalhara Chandrasekara
 * @see RouteDefinition
 * @see RouteHandler
 */
public final class AppRouter {

    /**
     * Represents the result of a successful route match, containing the handler and extracted parameters.
     */
    public static class RouteMatchResult {
        public final RouteHandler handler;
        public final RouteParams params;

        public RouteMatchResult(RouteHandler handler, RouteParams params) {
            this.handler = handler;
            this.params = params;
        }
    }

    private static final AppRouter instance = new AppRouter();

    private final List<RouteDefinition> routeDefinitions = new ArrayList<>();

    private AppRouter() {
    }

    /**
     * Returns the singleton instance of the AppRouter.
     *
     * @return the shared router instance
     */
    public static AppRouter getInstance() {
        return AppRouter.instance;
    }

    /**
     * Registers a new route with the given domain, HTTP method, URL pattern, and handler.
     *
     * @param domain     the domain to match (e.g., "api.example.com")
     * @param method     the HTTP method (e.g., GET, POST)
     * @param urlPattern the URL pattern with parameters (e.g., "/users/:id")
     * @param handler    the function to handle matching requests
     * @return {@code true} if the route was added successfully
     */
    public boolean register(String domain, HttpMethod method, String urlPattern, RouteHandler handler) {

        return routeDefinitions.add(new RouteDefinition(domain, method, urlPattern, handler));
    }

    /**
     * Attempts to match a request to a registered route.
     *
     * @param domain the request's target domain
     * @param method the request's HTTP method
     * @param path   the request's URL path
     * @return a {@link RouteMatchResult} if a match is found, or {@code null} otherwise
     */
    public RouteMatchResult match(String domain, HttpMethod method, String path) {
        for (RouteDefinition route : routeDefinitions) {
            if (route.getDomain().equals(domain) && route.getMethod() == method) {
                Matcher matcher = route.getPattern().matcher(path);
                if (matcher.matches()) {
                    return new RouteMatchResult(route.getHandler(), route.extractParams(matcher));
                }
            }
        }
        return null;
    }
}
