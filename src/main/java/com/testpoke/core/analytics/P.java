package com.testpoke.core.analytics;

import android.content.Context;
import com.testpoke.TestPoke;
import com.testpoke.api.Session;
import com.testpoke.api.SessionContext;
import com.testpoke.api.Sessions;
import com.testpoke.core.util.log.TP;

/*
 * Created by Jansel Valentin on 6/8/14.
 */
public final class P extends SessionProvider {
    private static final P single = new P();

    private Object lock = new Object();

    private SessionImp active;

    private P(){}

    static P p() {
        return single;
    }


    public static void injectProvider(SessionProvider provider, Object allowedOwner) {
        if (!(allowedOwner instanceof Sessions))
            throw new IllegalArgumentException("Instance that try to be owner of provider is not allowed");
        provider.setBase(single);
    }


    public Session getActive(Context context) {
//        if (!TestPoke.isRunning()) {
//            ML.w("There aren't active session, TestPoke hasn't takeoff");
//            return null;
//        }


        if (null != active)
            return active;


        if (TestPoke.getSettings().getOptions().isSessionAutoHandled() && TestPoke.isRunning() ) {
            synchronized (lock) {
                if (null == active) {
                    active = new SessionImp(context);
                    active.touchOpenTime();
                    /**
                     * Sync for the first time session creation, to be sure the first state reported to
                     * all session component is NEW not one that trust process or other process will report first
                     */
                    OpenSession openSession = new OpenSession(SimpleSchedulerWrapper.shared().getSchedulerBase(),context);
                    openSession.performTask();
                    //Above method is preferred in this situation
//                    SessionUtils.openSession(context, SimpleSchedulerWrapper.shared().getSchedulerBase(), -1);
                }
            }
        }
        return active;
    }


    public SessionContext getUnHandledContext() {
        if (TestPoke.getSettings().getOptions().isSessionAutoHandled())
            return null;
        return UnHandledSessionContext.unHandledContext;
    }


    private Session openSession(Context context) {
        if (!TestPoke.isRunning()) {
            TP.w("Not possible open new session, TestPoke hasn't takeoff");
            return null;
        }

        if (TestPoke.getSettings().getOptions().isSessionAutoHandled()) {
            TP.w("Attempt to open new session manually failed due to auto handled mode, returning active session");
            return getActive(context);
        }

        if (null == active) {
            synchronized (this) {
                if (null == active) {
                    active = new SessionImp(context);
                    active.touchOpenTime();
                    reportPlaceboState(SessionImp.STATE_WEAK);
                    SessionUtils.openSession(context, SimpleSchedulerWrapper.shared().getSchedulerBase(), -1);
                }
            }
            return active;
        }

        if (SessionImp.STATE_CLOSED == active.getState()) {
            active.touchOpenTime();

            SessionUtils.openSession(context, SimpleSchedulerWrapper.shared().getSchedulerBase(), -1);

            reportPlaceboState(SessionImp.STATE_WEAK);
        } else {
            TP.w("Attempt to open new session failed due to active session is open");
        }
        return active;
    }


    private void closeActiveSession() {
        if (!TestPoke.isRunning()) {
            TP.w("There aren't active session to close, TestPoke hasn't takeoff");
            return;
        }

        if (TestPoke.getSettings().getOptions().isSessionAutoHandled()) {
            TP.w("Can't close session manually in auto handled mode");
            return;
        }

        synchronized (this) {
            if (null != active && active.isOpen()) {
                SessionUtils.closeSession(active.getContext(), SimpleSchedulerWrapper.shared().getSchedulerBase(), -1);

                reportPlaceboState(SessionImp.STATE_CLOSED);
            } else {
                TP.w("There aren't active session to be closed");
            }
        }
    }


    private void reportPlaceboState(final int state) {
        if (null == active)
            return;
        active.reportState(new SessionImp.StateReporter() {
            @Override
            public int reportedState() {
                return state;
            }
        });
    }


    void destroy() {
        lock = null;
        if( null != active )
            active.destroy();
        /**
         * Avoiding race condition raised on {@link TPThread#destroyState()} when
         * CloseSession is sent to Scheduler  in background and delay more time that
         * P.p().destroy(); sentence is reached and set to null active session and destroy de session state
         */
        //active = null;
    }

    private static class UnHandledSessionContext implements SessionContext {
        private static final SessionContext unHandledContext = new UnHandledSessionContext();

        @Override
        public com.testpoke.api.Session openSession(Context context) {
            return single.openSession(context);
        }

        @Override
        public void closeActiveSession() {
            if( null != single )
                single.closeActiveSession();
        }
    }
}
