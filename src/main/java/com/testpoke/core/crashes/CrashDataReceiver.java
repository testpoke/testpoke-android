package com.testpoke.core.crashes;

/*
 * Created by Jansel Valentin on 5/20/14.
 */
/*package*/ interface CrashDataReceiver<T extends CrashData> {
    public void receive(T data);
    public void handleUnsentCrash(T data);
}
