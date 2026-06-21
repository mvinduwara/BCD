package lk.dev.bcd.cdi;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class Logger {
public void log(@Observes String message) {
    System.out.println("[LOGGER] " + message);
}
}
