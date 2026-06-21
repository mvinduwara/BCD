package lk.dev.bcd.cdi;

import jakarta.enterprise.context.Dependent;
import lk.dev.bcd.annotations.Email;

@Email
@Dependent
public class EmailNotifier implements NotificationService{
    @Override
    public void notify(String message) {
        System.out.println("Email Notifier: " + message);
    }
}
