package com.testpoke.core.analytics;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import com.testpoke.TestPoke;
import com.testpoke.core.util.log.TP;

/*
 * Created by Jansel Valentin on 6/30/2014.
 */

final class IdleWatcherMonitor {
    private static final IdleWatcherMonitor single = new IdleWatcherMonitor();

    private SimpleSchedulerWrapper scheduler;
    private Context context;
    private IdleWatcher idleWatcher;

    private IdleWatcherComponentCallback iwcc;

    public static IdleWatcherMonitor monitor() {
        return single;
    }


    @SuppressWarnings("NewApi")
    void begin(Context context, SimpleSchedulerWrapper scheduler) {
        if( null != idleWatcher || !TestPoke.getSettings().getOptions().isSessionAutoHandled() )
            return;

        this.context = context;
        this.scheduler = scheduler;

        idleWatcher = new IdleWatcher(context,isICSOrGreater());
        scheduler.scheduleImmediate(idleWatcher);

        if( isICSOrGreater() ){
            this.context.registerComponentCallbacks(iwcc = new IdleWatcherComponentCallback());
        }
    }

    @SuppressWarnings("NewApi")
    void end() {
        if (null != idleWatcher) {
            idleWatcher.stopWatch();
            idleWatcher = null;
        }
        if(isICSOrGreater() && null != iwcc)
            context.unregisterComponentCallbacks(iwcc);
    }


    private boolean isICSOrGreater(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }


    @SuppressWarnings("NewApi")
    private class IdleWatcherComponentCallback implements ComponentCallbacks2 {
        @Override
        public void onTrimMemory(int level) {
            if (ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN == level) {
                TP.i("Application goes to background");

               idleWatcher.reloadInterval();
               scheduler.scheduleImmediate( idleWatcher );
            }
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {}
        @Override
        public void onLowMemory() {}
    }

}
