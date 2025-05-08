package routers;

import controllers.UserController;
import io.github.vishvakalhara.olympus_router.util.HttpMethod;
import middlewares.ProtectByApiKey;
import utils.Router;

import javax.servlet.ServletException;

public class UserRouter extends Router {

    private final UserController userController;

    public UserRouter(String domain) {
        super(domain);
        userController = new UserController();
    }

    @Override
    public void init() throws ServletException {

        this.use(ProtectByApiKey::doMiddleware)
                .register(HttpMethod.GET, "/users", userController::getAllUsers)
                .register(HttpMethod.GET, "/:id", userController::getAllUsers);
    }
}
