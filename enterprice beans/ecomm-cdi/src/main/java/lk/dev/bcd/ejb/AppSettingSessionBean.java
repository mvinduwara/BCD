package lk.dev.bcd.ejb;

import jakarta.ejb.Singleton;
import lk.dev.bcd.ejb.remote.AppSetting;

@Singleton
public class AppSettingSessionBean implements AppSetting {

    @Override
    public String getName() {
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
