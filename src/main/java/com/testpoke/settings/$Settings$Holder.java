package com.testpoke.settings;

import android.content.Context;
import com.testpoke.core.util.log.TP;
import com.testpoke.settings.validator.SettingsValidator;

import java.lang.ref.WeakReference;

/*
 * Created by Jansel Valentin on 4/17/14.
 */
public final class $Settings$Holder {

    static{
        try{
            Class.forName("com.testpoke.core.settings.validator.SettingsValidatorAgregator", true, $Settings$Holder.class.getClassLoader());
        }catch ( ClassNotFoundException cne ){
            TP.e("There are missing dependencies classes", cne);
        }
    }

    private static WeakReference<ConfigSettings> defaultSettingsRef;

    private static WeakReference<SettingsValidator> defaultValidatorRef;

    private static Context forwardWeekContext;

    private static String directAppToken;



    public static void setDefaultSettings(ConfigSettings settings) {
        if (null != defaultSettingsRef )
            throw new IllegalStateException("Config Settings  already set");
        defaultSettingsRef = new WeakReference<ConfigSettings>(settings);
    }

    public static ConfigSettings getDefaultSettings() {
        return defaultSettingsRef.get();
    }


    public static void setDefaultValidator( SettingsValidator validator ){
        if( null != defaultValidatorRef )
            throw new IllegalStateException( "Config Setting Validator  already set" );
        defaultValidatorRef = new WeakReference<SettingsValidator>(validator);
    }

    public static SettingsValidator getDefaultValidator(){
        return defaultValidatorRef.get();
    }

    public static void setForwardWeekContext( Context context ){
        forwardWeekContext = context;
    }

    public static Context getForwardWeekContext(){
        return forwardWeekContext;
    }

    public static void releaseForwardWeekContext(){
        forwardWeekContext = null;
    }

    public static String getDirectAppToken() {
        return directAppToken;
    }

    public static void setDirectAppToken(String directAppToken) {
        $Settings$Holder.directAppToken = directAppToken;
    }
}
