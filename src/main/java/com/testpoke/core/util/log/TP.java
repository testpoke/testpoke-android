package com.testpoke.core.util.log;

public final class TP {
    private static final String LOG_TAG = "TestPoke SDK";

    public static int d(String msg) {
        return Log.debug(LOG_TAG, msg);
    }

    public static int d(String msg, Throwable thr) {
        return Log.debug(LOG_TAG, msg, thr);
    }

    public static int e(String msg) {
        return Log.error(LOG_TAG, msg);
    }

    public static int e(String msg, Throwable thr) {
        return Log.error(LOG_TAG, msg, thr);
    }

    public static int i(String msg) {
        return Log.info(LOG_TAG, msg);
    }

    public static int i(String msg, Throwable thr) {
        return Log.info(LOG_TAG, msg, thr);
    }

    public static int v(String msg) {
        return Log.verbose(LOG_TAG, msg);
    }

    public static int v(String msg, Throwable thr) {
        return Log.verbose(LOG_TAG, msg, thr);
    }

    public static int w(String msg) {
        return Log.warning(LOG_TAG, msg);
    }

    public static int w(String msg, Throwable thr) {
        return Log.warning(LOG_TAG, msg, thr);
    }
}
