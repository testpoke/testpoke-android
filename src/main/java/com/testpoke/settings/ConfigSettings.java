package com.testpoke.settings;

import android.content.Context;
import com.testpoke.core.util.log.TP;

/*
 * Created by Jansel Valentin on 4/17/14.
 */
public abstract class ConfigSettings implements Settings {

    private static ConfigSettings defaultSettings;

    public static ConfigSettings loadDefaultSettings(Context context) {
        if (null != defaultSettings)
            return defaultSettings;

        synchronized (ConfigSettings.class) {
            if (null == defaultSettings) {
                $Settings$Holder.setForwardWeekContext(context);

                try {
                    Class.forName("com.testpoke.core.settings.ConfigSettingsImp", true, ConfigSettings.class.getClassLoader());
                } catch (ClassNotFoundException cne) {
                    TP.e("There are missing dependencies classes", cne);
                }
                $Settings$Holder.releaseForwardWeekContext();

                defaultSettings = $Settings$Holder.getDefaultSettings();
                $Settings$Holder.getDefaultValidator().validate(defaultSettings);
            }
        }
        return defaultSettings;
    }

    public static ConfigSettings loadDefaultSettings(Context context, String appToken) {
        $Settings$Holder.setDirectAppToken(appToken);
        return loadDefaultSettings(context);
    }

    @Override
    public TestPokeOptions getOptions() {
        return defaultSettings.getOptions();
    }
}
