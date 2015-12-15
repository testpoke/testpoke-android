package com.testpoke.api;

import android.content.Context;
import com.testpoke.TestPoke;
import com.testpoke.core.analytics.KNULLS;
import com.testpoke.core.analytics.P;

/*
 * Created by Jansel Valentin on 6/8/14.
 */
public final class Sessions {

    private static Sessions owner = new Sessions();

    private static final SessionProvider single = new SessionProvider();

    static {
        SessionProvider.tryProviderInjection();
    }

    private Sessions() {
    }

    public static Session getActive(Context context) {
        if(!TestPoke.isRunning() || null == single.getActive(context) )
            return KNULLS._N;
        return single.getActive(context);
    }


    public static SessionContext getUnHandledContext() {
        return single.getUnHandledContext();
    }


    private static class SessionProvider extends com.testpoke.core.analytics.SessionProvider {
        private SessionProvider() {
        }

        private static void tryProviderInjection() {
            P.injectProvider(single, owner);
            owner = null;
        }

        @Override
        public Session getActive(Context context) {
            return getBase().getActive(context);
        }

        @Override
        public SessionContext getUnHandledContext() {
            return getBase().getUnHandledContext();
        }
    }
}
