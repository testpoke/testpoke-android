package com.testpoke.core.crashes;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.text.TextUtils;
import android.text.format.Time;
import com.testpoke.TestPoke;
import com.testpoke.core.util.Dump;
import com.testpoke.core.util.log.TP;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/*package*/ class FieldStatesProvider implements CrashDataProvider<AfterCrashData> {
    private Context context;

    public FieldStatesProvider(Context cxt) {
        context = cxt;
    }

    public AfterCrashData get(Thread broken, Throwable thr) {
        Time crashTime = new Time();
        crashTime.setToNow();

        final AfterCrashData data = new AfterCrashData();
        data.setCrashTime(crashTime);

        if( TestPoke.getSettings().getOptions().reportCrashDeviceConfig()) {
            data.setConfiguration(getConfiguration());
            data.setDeviceState(getDeviceState());
        }

        if (TestPoke.getSettings().getOptions().reportCrashStackTrace())
            data.setStackTrace(Dump.getStacktraceLastThrowableCause(thr));

        if (TestPoke.getSettings().getOptions().reportCrashPreferences())
            data.setSharedPreferences(getSharedPreferences());


        return data;
    }

    private Map<String,Object> getSharedPreferences() {
        TP.i("Collecting Crash SharedPreferences State");

        final String exclusion = TestPoke.getSettings().getOptions().crashPreferencesExcludePattern();

        String[] prefs = new File(context.getApplicationInfo().dataDir, "shared_prefs").list(new FilenameFilter() {
            public boolean accept(File file, String name) {
                String clippedName = name.substring(0, name.lastIndexOf('.'));
                boolean include = true;

                include &= !Pattern.compile(exclusion).matcher(clippedName).find();
                include |= TextUtils.isEmpty(exclusion);
                include &= !clippedName.startsWith("com.testpoke.");
                return include;
            }
        });


        Map<String,Object> jPrefs = new HashMap<String, Object>();


        for (int i = 0; null != prefs && prefs.length > i; ++i) {
            String pref = prefs[i];

            String clippedName = pref.substring(0, pref.lastIndexOf('.') );

            SharedPreferences sharedPreferences = context.getSharedPreferences(clippedName, Context.MODE_PRIVATE);

            clippedName = clippedName.replaceAll("\\.","_");
            dumpSharedPreferences(jPrefs, clippedName,sharedPreferences);
        }
        return jPrefs;
    }


    private Map<String,String> getConfiguration() {
        TP.i("Collecting Crash Time Device Configuration");

        Map<String,String> config;
        try{
            config = ConfigurationCollector.collectConfiguration(context);
        }catch ( Exception ex ){
            Dump.printStackTraceCause(ex);
            config = new HashMap<String, String>();
        }
        return config;
    }


    private Map<String,Object> getDeviceState(){
        Map<String,Object> states = new HashMap<String, Object>();
        states.put( "battery_level",getBatteryLevel() );
        return states;
    }


    //Taken from http://stackoverflow.com/questions/15746709/get-battery-level-only-once-using-android-sdk
    public float getBatteryLevel() {
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float)level / (float)scale) * 100.0f;
    }



    private void dumpSharedPreferences(Map<String,Object> jPref, String name,SharedPreferences prefs) {

        Map<String,String> values = new HashMap<String, String>();

        Set<String> keys = prefs.getAll().keySet();
        Iterator<String> it = keys.iterator();

        while (it.hasNext()) {
            String key = it.next();
            key = key.replaceAll("\\.","_");
            values.put(key, prefs.getAll().get(key) + "");
        }
        jPref.put(name, values);
    }
}
