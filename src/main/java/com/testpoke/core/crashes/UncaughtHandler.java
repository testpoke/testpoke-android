package com.testpoke.core.crashes;

import android.os.Process;
import com.testpoke.TestPoke;
import com.testpoke.core.util.Dump;
import com.testpoke.core.util.log.TP;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/*
 * Created by Jansel Valentin on 5/4/14.
 */

final class UncaughtHandler<T extends CrashData> implements Thread.UncaughtExceptionHandler {

    private static final int FIXED_WORKERS = 1;

    private Thread.UncaughtExceptionHandler previousHandler;
    private CrashHook crashHook;
    private CrashDataProvider<T> dataProvider;
    private List<CrashDataReceiver<T>> receivers = new ArrayList<CrashDataReceiver<T>>();
    private boolean alreadyCrashing;


    /*package*/ UncaughtHandler() {
        previousHandler = Thread.getDefaultUncaughtExceptionHandler();
        if( Thread.currentThread()  instanceof CrashHook) {
            crashHook = (CrashHook) Thread.currentThread();
        }
    }


    /*package*/ UncaughtHandler(CrashDataProvider<T> provider) {
        this();
        dataProvider = provider;
    }


    @Override
    public void uncaughtException(final Thread thread, final Throwable throwable) {
        if (alreadyCrashing) {
            return;
        }
        if( !TestPoke.isRunning() ){
            forwardException(thread,throwable);
            return;
        }

        alreadyCrashing = true;

        TP.i("TestPoke Caught Exception on Crash " + throwable.getClass());

        final CountDownLatch latch = new CountDownLatch(FIXED_WORKERS);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await();

                    propagateException(thread, throwable);
                } catch (Exception ex) {
                    Dump.printStackTraceCause(ex);
                    TP.e("Error waiting for crash publication be done.");
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                publishData(thread, throwable);

                latch.countDown();
            }
        }).start();
    }


    public void registerReceiver(CrashDataReceiver<T> receiver) {
        if (null == receiver || receivers.contains(receiver))
            return;
        receivers.add(receiver);
    }

    public void unregisterReceiver(CrashDataReceiver<T> receiver) {
        if (0 == receivers.size())
            return;
        receivers.remove(receiver);
    }


    private void clearReceivers() {
        receivers.clear();
    }


    private void publishData(Thread broken, Throwable thr) {
        CrashData data = null;
        CrashDataReceiver latestReceiver = null;

        try {
            data = dataProvider.get(broken, thr);
            for (int i = receivers.size() - 1; 0 <= i; --i) {
                latestReceiver = receivers.get(i);
                latestReceiver.receive(data);
            }
        } catch (Throwable re) {
            if( null != data && null != latestReceiver ) {
                latestReceiver.handleUnsentCrash(data);
            }

            propagateException(broken, thr);
            TP.i("Caught Exception while getting provider data and publishing to receivers ", re);
        }
    }

    private void forwardException(Thread broken, Throwable thr ){
        previousHandler.uncaughtException(broken, thr);
        exit();
    }

    private void propagateException(Thread broken, Throwable thr) {
        clearReceivers();
        TestPoke.end();
        previousHandler.uncaughtException(broken, thr);
        exit();
    }


    private void exit() {
        Process.killProcess(Process.myPid());
        System.exit(10);
    }

    /*package*/ CrashDataProvider<T> getDataProvider() {
        return dataProvider;
    }

    /*package*/ void setDataProvider(CrashDataProvider<T> dataProvider) {
        this.dataProvider = dataProvider;
    }

    /*package*/ CrashHook getCrashHook(){return crashHook; }

}

