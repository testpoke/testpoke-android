package com.testpoke.core.analytics;

import android.content.Context;
import com.testpoke.api.Session;
import com.testpoke.api.SessionContext;

/*
 * Created by Jansel Valentin on 6/8/14.
 */
public abstract class SessionProvider {

    private SessionProvider mBase;

    public abstract Session getActive(Context context);

    public abstract SessionContext getUnHandledContext();

    protected SessionProvider getBase(){
        return mBase;
    }

    void setBase( SessionProvider provider ){
        mBase = provider;
    }
}
