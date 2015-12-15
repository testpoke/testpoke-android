package com.testpoke.core.settings;

import com.testpoke.settings.$Settings$Holder;
import com.testpoke.settings.ConfigSettings;
import com.testpoke.settings.TestPokeOptions;

/*
 * Created by Jansel Valentin on 4/17/14.
 */
/*package*/ class ConfigSettingsImp extends ConfigSettings {


    static {
        $Settings$Holder.setDefaultSettings(loadSettings());
    }

    private TestPokeOptionsImp defaultOptions;

    /*package*/ ConfigSettingsImp() {
    }

    private static ConfigSettings loadSettings() {
        if( null != $Settings$Holder.getDirectAppToken() )
            return ConfigLoader.loadSettings($Settings$Holder.getDirectAppToken());

        return ConfigLoader.loadSettings($Settings$Holder.getForwardWeekContext());
    }

    /*package*/ TestPokeOptionsImp getTestPokeOptions() {
        if (null == defaultOptions)
            defaultOptions = new TestPokeOptionsImp(this);
        return defaultOptions;
    }

    @Override
    public TestPokeOptions getOptions() {
        return defaultOptions;
    }
}
