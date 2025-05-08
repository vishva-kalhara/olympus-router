package middlewares;

import io.github.vishvakalhara.olympus_router.util.Middleware;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ProtectByApiKey extends Middleware {

    private static final ProtectByApiKey instance = new ProtectByApiKey();

    private ProtectByApiKey() {
    }

    public static ProtectByApiKey getInstance(){
        return instance;
    }

    @Override
    public boolean doMiddleware(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        return false;
    }
}
