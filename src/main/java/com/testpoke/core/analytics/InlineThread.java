package com.testpoke.core.analytics;

import android.content.Context;
import com.testpoke.core.Injectable;
import com.testpoke.core.activation.StatesActivator;
import com.testpoke.core.crashes.CrashData;
import com.testpoke.core.crashes.CrashHook;

/*
 * Created by Jansel Valentin on 6/5/2014.
 */
final class InlineThread extends Thread implements CrashHook {

    private Context context;
    private Object mainLock;
    private Dispatcher dispatcher;
    private static InlineThread mDefault;


    public static InlineThread getInstance(Context context, Object mainLock) {
        if (null != mDefault)
            return mDefault;
        synchronized (InlineThread.class) {
            if (null == mDefault)
                mDefault = new InlineThread(context, mainLock);
        }
        return mDefault;
    }

    public static InlineThread getSynInstance(Context context){
        return mDefault;
    }


    static void checkCaller(Injectable<Context> caller) {
        if (!(caller instanceof NaiveCaller))
            throw new IllegalArgumentException("Caller is not allowed to create Dispatcher Instance");
    }


    private InlineThread(Context context, Object mainLock) {
        this.mainLock = mainLock;
        this.context = context;
        dispatcher = new Dispatcher(new NaiveCaller(context));
        start();
    }


    Dispatcher getDispatcher() {
        return dispatcher;
    }

    void terminate(){
        if( null != dispatcher )
            dispatcher.destroy();
    }

    @Override
    public void run() {
        synchronized (mainLock) {
           StatesActivator.activate(context);
            mainLock.notifyAll();
        }
    }


    @Override
    public void hookQuickly( CrashData data ) {
        dispatcher.dispatch(data);
        dispatcher = null;
    }


    private class NaiveCaller implements Injectable<Context> {
        Context context;

        NaiveCaller(Context context) {
            this.context = context;
        }

        @Override
        public Context get() {
            return context;
        }
    }
}
