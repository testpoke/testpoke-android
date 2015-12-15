package com.testpoke.core.util.log;

interface LogLevel {
    int level();

    int log(String tag, String msg, LogLevel call);

    int log(String tag, String msg, Throwable tr, LogLevel call);
}
