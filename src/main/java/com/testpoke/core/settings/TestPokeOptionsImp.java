package com.testpoke.core.settings;

import com.testpoke.settings.TestPokeOptions;

/*
 * Created by Jansel Valentin on 4/17/14.
 */
/*package*/ class TestPokeOptionsImp extends TestPokeOptions {


    /*package*/ private ConfigSettingsImp settings;

    /*package*/ int logLevel;
    /*package*/ String appToken;
    /*package*/ long sessionTimeout = 30000;
    /*package*/ boolean isDisabled = false;
//    /*package*/ boolean reportLogcatAlerts = false;
//    /*package*/ boolean isMonitorMemEnabled = false;
    /*package*/ boolean autoHandleSession = true;
    /*package*/ boolean reportCrashPreferences = false;
    /*package*/ boolean reportCrashStackTrace = true;
    /*package*/ boolean reportCrashDeviceConfig = false;
    /*package*/ String crashPreferenceExcludePattern = "";


    /*package*/ TestPokeOptionsImp(ConfigSettingsImp setting) {
        this.settings = setting;
    }

    @Override
    public String appToken() {
        return appToken;
    }

    @Override
    public int logLevel() {
        return logLevel;
    }

    @Override
    public boolean isDisabled() {
        return isDisabled;
    }
//
//    @Override
//    public boolean reportLogcatAlerts() {
//        return reportLogcatAlerts;
//    }

    @Override
    public boolean isSessionAutoHandled() {
        return autoHandleSession;
    }

//    @Override
//    public boolean isMonitorMemEnabled() {
//        return isMonitorMemEnabled;
//    }

    @Override
    public long sessionTimeout() {
        return sessionTimeout;
    }

    @Override
    public boolean reportCrashPreferences() {
        return reportCrashPreferences;
    }

    @Override
    public String crashPreferencesExcludePattern() {
        return crashPreferenceExcludePattern;
    }

    @Override
    public boolean reportCrashStackTrace() {
        return reportCrashStackTrace;
    }

    @Override
    public boolean reportCrashDeviceConfig() {
        return reportCrashDeviceConfig;
    }
}

