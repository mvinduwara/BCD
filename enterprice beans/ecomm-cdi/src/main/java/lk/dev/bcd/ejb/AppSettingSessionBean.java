package lk.dev.bcd.ejb;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import lk.dev.bcd.annotations.Email;
import lk.dev.bcd.cdi.EmailNotifier;
import lk.dev.bcd.cdi.MyService;
import lk.dev.bcd.cdi.NotificationService;
import lk.dev.bcd.cdi.SMSNotifier;
import lk.dev.bcd.ejb.remote.AppSetting;

@Singleton
public class AppSettingSessionBean implements AppSetting {

    private MyService myService;

    @Inject
    public void init() {
        myService = new MyService();
    }

//    @Inject
//    private EmailNotifier emailNotifier;

//    @Inject
//    private SMSNotifier smsNotifier;

    @Inject
    private Event<String> event;

    @Inject
    @Email
    private NotificationService notificationService;

    @Override
    public String getName() {
        myService.doSomething();
        notificationService.notify("Hello This Is My App Setting Session Bean");
        event.fire("Hello This Is My App Setting Session Bean");
        return "Ecomm EE App";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getDescription() {
        return "This Is The Ecomm EE App Setting Bean";
    }
}
