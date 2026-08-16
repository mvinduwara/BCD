package lk.jiat.web.config;

import jakarta.annotation.security.DeclareRoles;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath(("/api"))
@DeclareRoles({"ADMIN","USER"})
public class RestApplication extends Application {
}
