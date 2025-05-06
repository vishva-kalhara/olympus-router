package io.github.vishvakalhara.util;

import java.util.HashMap;

/**
 * A specialized map for storing route parameters extracted from URL patterns.
 * Keys represent parameter names (e.g., "id" from "/users/:id"), and values are the actual values
 * extracted from the request URL.
 *
 * <p>Example usage:
 * <pre>{@code
 * RouteParams params = new RouteParams();
 * params.put("id", "123");
 * String userId = params.get("id");  // "123"
 * }</pre>
 *
 * @author Wishva Kalhara Chandrasekara
 * @see HashMap
 * @since 1.0
 */
public class RouteParams extends HashMap<String, String> {
}
