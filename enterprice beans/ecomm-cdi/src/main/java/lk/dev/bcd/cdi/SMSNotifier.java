package lk.dev.bcd.cdi;

import jakarta.enterprise.context.Dependent;
import lk.dev.bcd.annotations.SMS;

import javax.management.Notification;

@SMS
@Dependent
public class SMSNotifier implements NotificationService {

    @Override
    public void notify(String message) {
        System.out.println("SMS Notifier: " + message);
    }
}
