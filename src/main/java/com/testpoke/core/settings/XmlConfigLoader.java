package com.testpoke.core.settings;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import com.testpoke.core.analytics._K;
import com.testpoke.core.util.log.Log;
import com.testpoke.settings.ConfigSettings;
import com.testpoke.settings.ConfigSettingsException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/*
 * Created by Jansel Valentin on 5/29/14.
 */
final class XmlConfigLoader extends ConfigLoader {

    private static final String META_DATA_NAME = "com.testpoke.SETTINGS";
    private static final String TAG_TESTPOKE = "testpoke";
    private static final String TAG_SESSION = "session";
    private static final String TAG_CRASHES = "crashes";
    private static final String TAG_PREFERENCES = "preferences";
    private static final String TAG_STACKTRACE = "stacktrace";
    private static final String TAG_DEVICE_CONFIG = "device-config";

//    private static final String ATTR_API_TOKEN = "api-token";
    private static final String ATTR_APP_TOKEN = "app-token";
//    private static final String ATTR_API_DISABLED = "api-disabled";
    private static final String ATTR_DISABLED = "disabled";
//    private static final String ATTR_REPORT_LOGCAT_ALERTS = "report-lca"; //report-logcat-alerts="true"
    private static final String ATTR_LOG_LEVEL = "log-level";
    private static final String ATTR_AUTO_HANDLED = "auto-handled";
//    private static final String ATTR_MONITOR_MEM = "monitor-mem";
    private static final String ATTR_TIMEOUT = "timeout";
    private static final String ATTR_REPORT = "report";
    private static final String ATTR_EXCLUDE_PATTERN = "exclude-pattern";


    @Override
    public ConfigSettings load(Context context) {
        XmlPullParser parser;
        try {

            PackageManager pm = context.getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            parser = appInfo.loadXmlMetaData(pm, META_DATA_NAME);

        } catch (PackageManager.NameNotFoundException ex) {
            throw new ConfigSettingsException("Error trying to get meta-data for package " + context.getPackageManager(), ex);
        }
        if (null == parser)
            throw new ConfigSettingsException("Missing " + META_DATA_NAME + " meta-data in AndroidManifest.xml file");

        return parse(parser);
    }

    @Override
    public ConfigSettings createDirect(String appToken) {
        ConfigSettingsImp settings = new ConfigSettingsImp();
        TestPokeOptionsImp options = settings.getTestPokeOptions();
        options.appToken = appToken;
        return settings;
    }

    private ConfigSettings parse(XmlPullParser parser) {

        ConfigSettingsImp settings = new ConfigSettingsImp();
        TestPokeOptionsImp options = settings.getTestPokeOptions();

        try {

            if (XmlPullParser.START_DOCUMENT != parser.getEventType())
                throw new ConfigSettingsException("Malformed config file");

            parser.next();
            boolean canContinue = dumpGlobalAttributes(options, parser);

            if (!canContinue)
                return settings;

            canContinue = dumpSessionAttributes(options, parser);

            if (!canContinue)
                return settings;

            dumpCrashChildAttributes(options, parser);

        } catch (XmlPullParserException ex) {
            throw new ConfigSettingsException("Error parsing xml config file", ex);
        } catch (IOException ex) {
            throw new ConfigSettingsException("Error parsing xml config file", ex);
        }

        return settings;
    }


    private boolean dumpGlobalAttributes(TestPokeOptionsImp options, XmlPullParser parser) throws XmlPullParserException, IOException {
        if (XmlPullParser.START_TAG != parser.next())
            return false;


        if (!TAG_TESTPOKE.equals(parser.getName()))
            return false;


        Object value;

        if (null != (value = parser.getAttributeValue(null, ATTR_DISABLED))) {
            options.isDisabled = new Boolean(value.toString());
            if (options.isDisabled)
                return false;
        }

        if (null != (value = parser.getAttributeValue(null, ATTR_APP_TOKEN))) {
            options.appToken = value.toString();
        }

        if (null != (value = parser.getAttributeValue(null, ATTR_LOG_LEVEL))) {
            options.logLevel = mapLogLevel(value.toString());
        }

        return true;
    }


    private boolean dumpSessionAttributes(TestPokeOptionsImp options, XmlPullParser parser) throws XmlPullParserException, IOException {
        if (XmlPullParser.START_TAG != parser.next())
            return false;

        if (!TAG_SESSION.equals(parser.getName()))
            return false;

        Object value;

        if (null != (value = parser.getAttributeValue(null, ATTR_AUTO_HANDLED)))
            options.autoHandleSession = new Boolean(value.toString());

//        if (null != (value = parser.getAttributeValue(null, ATTR_REPORT_LOGCAT_ALERTS))) {
//            options.reportLogcatAlerts = new Boolean(value.toString());
//        }
//
//        if (null != (value = parser.getAttributeValue(null, ATTR_MONITOR_MEM))) {
//            options.isMonitorMemEnabled = new Boolean(value.toString());
//        }

        if (null != (value = parser.getAttributeValue(null, ATTR_TIMEOUT))) {
            try {
                options.sessionTimeout = Long.valueOf(value.toString());
            } catch (NumberFormatException ex) {
            }
            options.sessionTimeout = 0 > options.sessionTimeout ? 0 : options.sessionTimeout;
        }
        return true;
    }


    private boolean dumpCrashChildAttributes(TestPokeOptionsImp options, XmlPullParser parser) throws XmlPullParserException, IOException {
        if (XmlPullParser.START_TAG != parser.next())
            return false;

        if (!TAG_CRASHES.equals(parser.getName()))
            return true;


        boolean eof = false;
        Object value;

        while (!eof) {
            int eventType = parser.next();

            switch (eventType) {
                case XmlPullParser.START_TAG: {
                    if (TAG_PREFERENCES.equals(parser.getName())) {
                        if (null != (value = parser.getAttributeValue(null, ATTR_REPORT)))
                            options.reportCrashPreferences = new Boolean(value.toString());

                        if (null != (value = parser.getAttributeValue(null, ATTR_EXCLUDE_PATTERN)))
                            options.crashPreferenceExcludePattern = value.toString();
                    }

                    if (TAG_STACKTRACE.equals(parser.getName())) {
                        if (null != (value = parser.getAttributeValue(null, ATTR_REPORT)))
                            options.reportCrashStackTrace = new Boolean(value.toString());
                    }

                    if (TAG_DEVICE_CONFIG.equals(parser.getName())) {
                        if (null != (value = parser.getAttributeValue(null, ATTR_REPORT)))
                            options.reportCrashDeviceConfig = new Boolean(value.toString());
                    }
                    break;
                }
                case XmlPullParser.END_TAG:
                case XmlPullParser.END_DOCUMENT:
                    if (TAG_CRASHES.equals(parser.getName()))
                        eof = true;
                    break;
            }
        }
        return true;
    }


    private int mapLogLevel(String str) {
        if ("verbose".equals(str.toLowerCase())) return Log.VERBOSE;
        if ("debug".equals(str.toLowerCase())) return Log.DEBUG;
        if ("info".equals(str.toLowerCase())) return Log.INFO;
        if ("warn".equals(str.toLowerCase())) return Log.WARN;
        if ("error".equals(str.toLowerCase())) return Log.ERROR;
        if ("silent".equals(str.toLowerCase())) return Log.SILENT;
        return Log.VERBOSE;
    }


    @Override
    _K getLoadedK() {
        return new _KImp();
    }
}
