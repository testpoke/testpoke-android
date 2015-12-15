package com.testpoke.core.util.log;

enum StandardLogLevel implements LogLevel {


    VERBOSE{
        @Override
        int print(String tag, String msg) {
            return android.util.Log.v(tag, msg);
        }

        @Override
        int print(String tag, String msg, Throwable thr) {
            return android.util.Log.v(tag, msg,thr);
        }

        @Override
        public int level() {
            return Log.VERBOSE;
        }

        @Override
        public int log(String tag, String msg, LogLevel call) {
            return logInBestChainNode(tag, msg, this, call);
        }

        @Override
        public int log(String tag, String msg, Throwable tr, LogLevel call) {
            return logInBestChainNode(tag, msg,tr,this, call);
        }
    },
    DEBUG{
        @Override
        int print(String tag, String msg) {
            return android.util.Log.d(tag, msg);
        }

        @Override
        int print(String tag, String msg, Throwable thr) {
            return android.util.Log.d(tag, msg, thr);
        }

        @Override
        public int level() {
            return Log.DEBUG;
        }

        @Override
        public int log(String tag, String msg, LogLevel call) {
            return logInBestChainNode(tag, msg, this, call);
        }

        @Override
        public int log(String tag, String msg, Throwable tr, LogLevel call) {
            return logInBestChainNode(tag, msg, tr,this, call);
        }
    },
    INFO {
        @Override
        int print(String tag, String msg) {
            return android.util.Log.i(tag, msg);
        }

        @Override
        int print(String tag, String msg, Throwable thr) {
            return android.util.Log.i(tag, msg, thr);
        }

        @Override
        public int level() {
            return Log.INFO;
        }

        @Override
        public int log(String tag, String msg, LogLevel call) {
            return logInBestChainNode(tag,msg,this,call);
        }

        @Override
        public int log(String tag, String msg, Throwable tr, LogLevel call) {
            return logInBestChainNode(tag,msg,tr,this,call);
        }
    },
    WARN {
        @Override
        int print(String tag, String msg) {
            return android.util.Log.w(tag, msg);
        }

        @Override
        int print(String tag, String msg, Throwable thr) {
            return android.util.Log.w(tag, msg, thr);
        }

        @Override
        public int level() {
            return Log.WARN;
        }


        @Override
        public int log(String tag, String msg, LogLevel call) {
            return logInBestChainNode(tag,msg,this,call);
        }

        @Override
        public int log(String tag, String msg, Throwable tr, LogLevel call) {
            return logInBestChainNode(tag,msg,tr,this,call);
        }
    },
    ERROR {
        @Override
        int print(String tag, String msg) {
            return android.util.Log.e(tag, msg);
        }

        @Override
        int print(String tag, String msg, Throwable thr) {
            return android.util.Log.e(tag, msg, thr);
        }

        @Override
        public int level() {
            return Log.ERROR;
        }

        @Override
        public int log(String tag, String msg, LogLevel call) {
            return logInBestChainNode(tag,msg,this,call);
        }

        @Override
        public int log(String tag, String msg, Throwable tr, LogLevel call) {
            return logInBestChainNode(tag,msg,tr,this,call);
        }
    },
    SILENT {
        @Override
        int print(String tag, String msg) {
            return -1;
        }

        @Override
        int print(String tag, String msg, Throwable thr) {
            return -1;
        }

        @Override
        public int level() {
            return Log.SILENT;
        }

        @Override
        public int log(String tag, String msg, LogLevel call) {
            return logInBestChainNode(tag,msg,this,call);
        }

        @Override
        public int log(String tag, String msg, Throwable tr, LogLevel call) {
            return logInBestChainNode(tag,msg,tr,this,call);
        }
    };

//
    /*package*/
    abstract int print(String tag, String msg);

    /*package*/
    abstract int print(String tag, String msg, Throwable thr);


    protected int logInBestChainNode(String tag, String msg, StandardLogLevel mThis, LogLevel call) {
        if (mThis.level() > call.level())
            return -1;
        if (mThis.level() == call.level())
            return mThis.print(tag, msg);
        return call.log(tag, msg, call);
    }


    protected int logInBestChainNode(String tag, String msg, Throwable thr, StandardLogLevel mThis, LogLevel call) {
        if (mThis.level() > call.level())
            return -1;
        if (mThis.level() == call.level())
            return mThis.print(tag, msg, thr);
        return call.log(tag, msg, call);
    }


    /*package*/
    static LogLevel getMinLevel(int level) {
        for (StandardLogLevel mLevel : StandardLogLevel.values())
            if (level == mLevel.level())
                return mLevel;

        return VERBOSE;
    }
}
