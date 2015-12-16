package com.testpoke.settings;

/*
 * Created by Jansel Valentin on 4/17/14.
 */
public abstract class TestPokeOptions implements Options {

    public abstract String appToken();

    public abstract int logLevel();

    public abstract boolean isDisabled();

//    public abstract boolean reportLogcatAlerts();

    public abstract boolean isSessionAutoHandled();

    public abstract long sessionTimeout();

//    public abstract boolean isMonitorMemEnabled();

    public abstract boolean reportCrashPreferences();

    public abstract String crashPreferencesExcludePattern();

    public abstract boolean reportCrashStackTrace();

    public abstract boolean reportCrashDeviceConfig();
}
