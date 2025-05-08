package utils;

import io.github.vishvakalhara.olympus_router.RouterBase;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Router extends RouterBase {

    public Router(String domain) {
        super(domain);
    }

    @Override
    public void handleEndpointNotFoundException(HttpServletResponse resp) throws IOException {
        resp.getWriter().write("Endpoint not found!");
    }
}
