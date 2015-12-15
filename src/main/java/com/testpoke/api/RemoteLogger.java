package com.testpoke.api;

/*
 * Created by Jansel Valentin on 6/9/2014.
 */

public interface RemoteLogger {

    void d(String msg);

    void d(String tag, String msg);

    void e(String msg);

    void e(String tag, String msg);

    void i(String msg);

    void i(String tag, String msg);

    void v(String msg);

    void v(String tag, String msg);

    void w(String msg);

    void w(String tag, String msg);
}
