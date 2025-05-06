package io.github.vishvakalhara.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a route definition for handling HTTP requests.
 * This class encapsulates the domain, HTTP method, URL pattern, and handler for a route,
 * along with logic for extracting path parameters from matching URLs.
 *
 * @author Wishva Kalhara Chandrasekara
 */
public class RouteDefinition {

    private final String domain;

    private final HttpMethod method;

    private final RouteHandler handler;

    private final Pattern pattern;

    private final List<String> paramNames;

    /**
     * Constructs a new RouteDefinition with the given domain, HTTP method, URL pattern, and handler.
     * The URL pattern is parsed to extract parameter names and generate a regex pattern for matching.
     *
     * @param domain     the domain for this route (e.g., "api.example.com")
     * @param method     the HTTP method (e.g., GET, POST)
     * @param urlPattern the URL pattern (e.g., "/users/:id") where segments starting with ":" denote parameters
     * @param handler    the handler that will process requests for this route
     */
    public RouteDefinition(String domain, HttpMethod method, String urlPattern, RouteHandler handler) {
        this.domain = domain;
        this.method = method;
        this.handler = handler;

        StringBuilder regex = new StringBuilder();
        this.paramNames = new ArrayList<>();

        for (String part : urlPattern.split("/")) {
            if (part.startsWith(":")) {
                paramNames.add(part.substring(1));
                regex.append("/([^/]+)");
            } else if (!part.isEmpty()) {
                regex.append("/").append(part);
            }
        }

        this.pattern = Pattern.compile("^" + (regex.length() == 0 ? "/" : regex.toString()) + "$");
    }

    /**
     * Extracts route parameters from a URL that matches this route's pattern.
     *
     * @param matcher the regex matcher containing the matched URL groups
     * @return a {@link RouteParams} object containing the extracted key-value pairs
     */
    public RouteParams extractParams(Matcher matcher) {
        RouteParams params = new RouteParams();
        for (int i = 0; i < paramNames.size(); i++) {
            params.put(paramNames.get(i), matcher.group(i + 1));
        }
        return params;
    }

    /**
     * Returns the domain associated with this route.
     *
     * @return the domain (e.g., "api.example.com")
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Returns the HTTP method (GET, POST, etc.) for this route.
     *
     * @return the HTTP method
     */
    public HttpMethod getMethod() {
        return method;
    }

    /**
     * Returns the handler responsible for processing requests for this route.
     *
     * @return the route handler
     */
    public RouteHandler getHandler() {
        return handler;
    }

    /**
     * Returns the compiled regex pattern derived from the URL pattern.
     *
     * @return the regex pattern used for matching URLs
     */
    public Pattern getPattern() {
        return pattern;
    }
}
