package com.testpoke.core.crashes;

/*
 * Created by Jansel Valentin on 5/20/14.
 */
/*package*/ interface CrashDataProvider<T extends CrashData>{
    T get(Thread broken, Throwable thr);
}
