package com.testpoke.core.util.log;


public interface Logger {

    int debug(String tag, String msg);

    int debug(String tag, String msg, Throwable thr);

    int error(String tag, String msg);

    int error(String tag, String msg, Throwable thr);

    int info(String tag, String msg);

    int info(String tag, String msg, Throwable thr);

    int verbose(String tag, String msg);

    int verbose(String tag, String msg, Throwable thr);

    int warning(String tag, String msg);

    int warning(String tag, String msg, Throwable thr);

}
