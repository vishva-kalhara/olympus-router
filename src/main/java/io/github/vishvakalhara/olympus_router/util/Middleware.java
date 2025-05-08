package io.github.vishvakalhara.olympus_router.util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class Middleware {

    public abstract boolean doMiddleware(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException ;
}
