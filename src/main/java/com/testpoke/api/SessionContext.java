package com.testpoke.api;

import android.content.Context;

/*
 * Created by Jansel ValentinJansel R. Abreu (jrodr) on 5/31/2014.
 */
public interface SessionContext {
    Session openSession(Context context);

    void closeActiveSession();
}
