package com.testpoke.core.settings;

import android.content.Context;
import com.testpoke.core.analytics._K;
import com.testpoke.settings.ConfigSettings;

/*
 * Created by Jansel Valentin on 4/17/14.
 */
/*package*/ abstract class ConfigLoader {

    private static ConfigLoader defaultLoader;

    public static ConfigSettings loadSettings(Context context) {

        ConfigSettings settings  = getDefaultLoader().load( context );

        new Thread(new Runnable() {
            @Override
            public void run() {
                _KInjector.propagate(getDefaultLoader().getLoadedK());
            }
        }).start();

        return settings;
    }

    public static ConfigSettings loadSettings(String appToken){
        return getDefaultLoader().createDirect(appToken);
    }

    private static ConfigLoader getDefaultLoader(){
        if (null != defaultLoader)
            return defaultLoader;

        synchronized (ConfigLoader.class) {
            if (null == defaultLoader)
                defaultLoader = new XmlConfigLoader();
        }
        return defaultLoader;
    }

    public abstract ConfigSettings load(Context context);

    public abstract ConfigSettings createDirect(String appToken);

    _K getLoadedK(){
        return null;
    }
}
