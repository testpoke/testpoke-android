package com.testpoke.core.util.log;

/*
 * Created by Jansel Valentin on 5/9/14.
 * <p/>
 */
public final class Log {
    public static final int VERBOSE = 2;

    public static final int DEBUG = 3;

    public static final int INFO = 4;

    public static final int WARN = 5;

    public static final int ERROR = 6;

    public static final int SILENT = 7;

    private static Logger delegateLogger;

    private static LogLevel minLevel = StandardLogLevel.VERBOSE;



    /*package*/
    static void setLogLevel(LogLevel level) {
       minLevel = level;
    }

     public static int getLogLevel(){
         if( null != minLevel )
             return minLevel.level();
         return VERBOSE;
     }

    public static void setLogLevel(int level) {
        if (null != delegateLogger)
            return;
        minLevel = StandardLogLevel.getMinLevel(level);
    }

    /*package*/ static LogLevel getMinLogLevel() {
        return minLevel;
    }


    public static void setLogger( Logger logger ){
        delegateLogger = logger;
    }



    public static int debug(String tag, String msg) {
        if( null != delegateLogger )
            return delegateLogger.debug(tag,msg);

        return minLevel.log(tag,msg, StandardLogLevel.DEBUG);
    }



    public static int debug(String tag, String msg, Throwable thr){
        if( null != delegateLogger )
            return delegateLogger.debug(tag,msg,thr);

        return minLevel.log(tag,msg,thr, StandardLogLevel.DEBUG);
    }



    public static int error(String tag, String msg){
        if( null != delegateLogger )
            return delegateLogger.error(tag, msg);

        return minLevel.log(tag,msg, StandardLogLevel.ERROR);
    }



    public static  int error(String tag, String msg, Throwable thr){
        if( null != delegateLogger )
            return delegateLogger.error(tag,msg,thr);

        return minLevel.log(tag,msg,thr, StandardLogLevel.ERROR);
    }



    public static  int info(String tag, String msg){
        if( null != delegateLogger )
            return delegateLogger.info(tag, msg);

        return minLevel.log(tag,msg, StandardLogLevel.INFO);
    }



    public static  int info(String tag, String msg, Throwable thr){
        if( null != delegateLogger )
            return delegateLogger.info(tag,msg,thr);

        return minLevel.log(tag,msg,thr, StandardLogLevel.INFO);
    }



    public static  int verbose(String tag, String msg){
        if( null != delegateLogger )
            return delegateLogger.verbose(tag, msg);

        return minLevel.log(tag, msg, StandardLogLevel.VERBOSE);
    }


    public static int verbose(String tag, String msg, Throwable thr){
        if( null != delegateLogger )
            return delegateLogger.verbose(tag,msg,thr);

        return minLevel.log(tag,msg,thr, StandardLogLevel.VERBOSE);
    }


    public static int warning(String tag, String msg){
        if( null != delegateLogger )
            return delegateLogger.warning(tag, msg);

        return minLevel.log(tag,msg, StandardLogLevel.WARN);
    }


    public static int warning(String tag, String msg, Throwable thr){
        if( null != delegateLogger )
            return delegateLogger.warning(tag, msg, thr);

        return minLevel.log(tag,msg,thr, StandardLogLevel.WARN);
    }
}
