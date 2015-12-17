package com.testpoke.core.analytics;

import android.content.Context;
import android.os.Process;
import com.testpoke.TestPoke;
import com.testpoke.core.net.connectivity.ConnectionState;
import com.testpoke.core.net.connectivity.ConnectivityListener;
import com.testpoke.core.net.connectivity.ConnectivityWatcher;
import com.testpoke.core.schedule.event.Events;
import com.testpoke.core.util.log.TP;

/*
 * Created by Jansel Valentin on 5/6/14.
 */
public final class TPThread extends Thread implements AccessToken.OnAccessTokenListener, ConnectivityListener, SessionImp.StateReporter {
    private static final String NAME = "TestPoke Session Thread";

    private static TPThread tpThread;

    private SimpleSchedulerWrapper scheduler;

    private volatile boolean isServing;
    private volatile boolean trustedBegin;

    private int reportedState = SessionImp.STATE_WEAK;

    private Context context;
    private Object mainLock;

    private TPThread(Context context, Object lock) {
        this.mainLock = lock;
        tryStart(context);
    }

    public static TPThread getDefault(Context context, Object lock) {
        if (null != tpThread)
            return tpThread;

        synchronized (TPThread.class) {
            if (null == tpThread)
                tpThread = new TPThread(context, lock);
        }
        return tpThread;
    }


    private void tryStart(Context context) {
        if (isServing) {
            TP.w("Session Service is already running");
        } else {
            if (!TestPoke.isRunning()) {
                TP.d("Not allowed initiate MBThread with TestPoke hasn't takeoff");

                stopThread();
            } else {
                this.context = context;
                setName(NAME);
                setPriority(Process.THREAD_PRIORITY_BACKGROUND);

                start();
            }
        }
    }

    public void stopThread() {
        TP.d("Destroying " + NAME);

        if (isServing)
            revoke();

        if (TestPoke.isRunning()) {
            TestPoke.end();
        }
    }

    @Override
    public void run() {
        isServing = true;
        init();
    }


    public void onAccessToken(AccessToken at, int mode, int access, IA ia) {
        if (AccessToken.MODE_TRUST == mode && AccessToken.ACCESS_REJECTED == access) {
            ConnectivityWatcher.unregisterListener(this);

            /**
             * Changed order call, due on TestPoke.end() cleaned up all state listener on
             * P.p().destroy() before setReportedState(SessionImp.STATE_ABORTED) were called
             */
            setReportedState(SessionImp.STATE_ABORTED);

            TestPoke.end();

        } else if (AccessToken.MODE_TRUST == mode && AccessToken.ACCESS_RETRY_TIMEOUT == access) {
            setReportedState(SessionImp.STATE_WEAK);
        } else if (AccessToken.MODE_TRUST == mode && AccessToken.ACCESS_TRUSTED == access) {

            setReportedState(SessionImp.STATE_OPEN);
            TP.d("Application authenticated.!");


//        if( TestPoke.getSettings().getOptions().isMonitorMemEnabled() ) {
//            if( NetworkHelper.isWifi(context))
//                scheduler.schedule(new MemoryMonitor(context));
//            else{
//                ML.i( "Memory monitor was configured to true, but network type is not wifi" );
//                if(BuildConfig.DEBUG)
//                    ML.e( "Memory monitor was configured to true, but network type is not wifi" );
//            }
//        }

            InlineThread.getInstance(context, null).getDispatcher().dispatch();

        } else if (AccessToken.MODE_REVOKE == mode && AccessToken.ACCESS_REVOKED == access) {
            /*could never reached, because the service is destroyed before this execution take place*/
        }

        at.unregisterOnAccessTokenListener(this);
        scheduler.remove(at);
    }


    public void onChangeDetected(ConnectionState state) {
        if (ConnectionState.CONNECTED == state) {
            synchronized (this) {
                SessionImp active = ((SessionImp) P.p().getActive(context));

                if (trustedBegin || (null != active && active.wasCrashed()))
                    return;

                TP.i("Trusting application...");
                trust();
                trustedBegin = true;
            }

        } else {
            if (!trustedBegin)
                TP.w("Device is " + state + " from network, Application build trust scheduled for connection");
        }
    }

    public  int reportedState() {
        return reportedState;
    }


    private void init() {


        scheduler = SimpleSchedulerWrapper.shared();
        scheduler.start();
        Events.prepare(CloseOldestSession.class, scheduler.getSchedulerBase(), context).fireImmediate();


        liftOnActivation(context, mainLock);

        IdleWatcherMonitor.monitor().begin(context,scheduler);

        ConnectivityWatcher.registerListener(this);
    }


    private void destroyState() {
        isServing = false;

        IdleWatcherMonitor.monitor().end();

        if( null != InlineThread.getSynInstance(null) )
            InlineThread.getSynInstance(null).terminate();

        P.p().destroy();
    }


    private void trust() {
        AccessToken at = new AccessToken(context, AccessToken.MODE_TRUST);
        at.registerOnAccessTokenListener(this);
        scheduler.scheduleImmediate(at);
    }


    private static void liftOnActivation(Context context, Object mainLock) {
        InlineThread thread = InlineThread.getInstance(context, mainLock);
    }


    private void revoke() {
        destroyState();
//        AccessToken at = new AccessToken(context, AccessToken.MODE_REVOKE); Not supported now
//        scheduler.scheduleImmediate(at);
    }


    private void setReportedState(int state) {
        this.reportedState = state;
        SessionImp active = (SessionImp) P.p().getActive(context);
        if (null != active)
            active.reportState(this);
    }
}
