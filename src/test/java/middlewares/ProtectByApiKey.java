package middlewares;

import io.github.vishvakalhara.olympus_router.util.RouteHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ProtectByApiKey  {

    private static final ProtectByApiKey instance = new ProtectByApiKey();

    private ProtectByApiKey() {
    }

    public static ProtectByApiKey getInstance(){
        return instance;
    }

    public boolean doMiddleware(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        return true;
    }
}
