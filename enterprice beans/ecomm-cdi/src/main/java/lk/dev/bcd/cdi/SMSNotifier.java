package lk.dev.bcd.cdi;

import jakarta.enterprise.context.Dependent;

import javax.management.Notification;

@Dependent
public class SMSNotifier implements NotificationService {

    @Override
    public void notify(String message) {
        System.out.println("SMS Notifier: " + message);
    }
}
