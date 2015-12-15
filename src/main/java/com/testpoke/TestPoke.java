package com.testpoke;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import com.testpoke.api.RemoteLogger;
import com.testpoke.api.Session;
import com.testpoke.api.Sessions;
import com.testpoke.core.analytics.TPThread;
import com.testpoke.core.net.connectivity.ConnectivityWatcher;
import com.testpoke.core.util.Dump;
import com.testpoke.core.util.log.Log;
import com.testpoke.core.util.log.TP;
import com.testpoke.settings.ConfigSettings;
import com.testpoke.settings.ConfigSettingsException;


public final class TestPoke {

    private static final String version = "2.0.5";

    private static Application application;
    private static ConfigSettings settings;
    private static volatile boolean running;


    public static void init(Application app, ConfigSettings settings) {

        if (null != application)
        	return;
            //throw new IllegalStateException("TestPoke is already running");

        if (null == settings)
            throw new IllegalArgumentException("TestPoke ConfigsSettings must not be null");


        Log.setLogLevel(settings.getOptions().logLevel());
        TP.i("Configured TestPoke SDK Logger Level to " + Log.getLogLevel());

        if (settings.getOptions().isAPIDisabled()) {
            TP.i("TestPoke SDK is Disabled.");
            return;
        }

        TP.i("Initiating TestPoke SDK...");

        if (settings.getOptions().isSessionAutoHandled()) {
            TP.i("Auto handled sessions detected");
        } else {
            TP.i("Manual management sessions detected");
        }
        application = app;
        TestPoke.settings = settings;
        initiate();
    }

    public static void init(final Application app) {
        ConfigSettings config = ConfigSettings.loadDefaultSettings(app);
        init(app, config);
    }

    public static void init(final Application app, String appToken){
        ConfigSettings config = ConfigSettings.loadDefaultSettings(app, appToken);
        init(app, config);
    }


    public static void end() {
        if (!running)
            return;

        TP.i("TestPoke is landing...");
        running = false;
        TPThread.getDefault(application, null).stopThread();
        ConnectivityWatcher.stop(application);
    }

    public static boolean isRunning() {
        return running;
    }

    public static String getVersion() {
        return version;
    }

    public static ConfigSettings getSettings() {
        return settings;
    }

    public static Session getActiveSession(Context context) {
        return Sessions.getActive(context);
    }

    public static void sendEvent( String event ){
        Sessions.getActive(application).sendEvent(event);
    }

    public static void sendEvent( int level, String event ){
        Sessions.getActive(application).sendEvent(level,event);
    }

    public static  void sendEvent( String category, String event ){
        Sessions.getActive(application).sendEvent(category,event);
    }

    public static void sendEvent( int level, String category, String event ){
        Sessions.getActive(application).sendEvent(level,category,event);
    }

    public static void sendException(Throwable thr){
        Sessions.getActive(application).sendException(thr);
    }

    public static RemoteLogger logger(){
        return Sessions.getActive(application).logger();
    }

    public static void sendLog(String message){
         Sessions.getActive(application).logger().v(message);
    }

    public static void sendLog(String tag, String message){
        Sessions.getActive(application).logger().v(tag,message);
    }

    public static boolean isSessionOpen(){
        return null != Sessions.getActive(application) && Sessions.getActive(application).isOpen();
    }

    public static String getSessionId() {
        return Sessions.getActive(application).getId();
    }

    private static void initiate() {
        running = true;
        ConnectivityWatcher.start(application);
        final Object mainLock = new Object();
        synchronized (mainLock) {
            TPThread.getDefault(application, mainLock);
            try {
                mainLock.wait();
            } catch (InterruptedException ex) {
                end();
                Dump.printStackTraceCause(ex);
            }
        }
    }

    private static void guardManifestInvariant() {
        checkPermissionsGrant();
    }

    private static void checkPermissionsGrant() {
        PackageManager pm = application.getPackageManager();
        try {
            if (pm.checkPermission(android.Manifest.permission.READ_PHONE_STATE, application.getPackageName()) == PackageManager.PERMISSION_GRANTED)
                return;
        } catch (RuntimeException e) {
        }
        throw new ConfigSettingsException(android.Manifest.permission.READ_PHONE_STATE + " permission is required to get device identifier");
    }
}
