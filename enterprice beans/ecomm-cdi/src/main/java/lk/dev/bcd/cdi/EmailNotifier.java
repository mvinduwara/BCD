package lk.dev.bcd.cdi;

import jakarta.enterprise.context.Dependent;

@Dependent
public class EmailNotifier implements NotificationService{
    @Override
    public void notify(String message) {
        System.out.println("Email Notifier: " + message);
    }
}
