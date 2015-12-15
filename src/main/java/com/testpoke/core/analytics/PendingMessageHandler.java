package com.testpoke.core.analytics;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import com.testpoke.TestPoke;
import com.testpoke.core.util.log.TP;

import java.util.LinkedList;

/*
 * Created by Jansel Valentin on 6/10/2014.
 */
public abstract class PendingMessageHandler<Target> implements SessionImp.StateListener, Destroyable {

    private MHandler mHandler;

    protected final int type;
    protected SessionImp session;
    protected int reportedState;
    protected volatile boolean operationsCancelled;
    private LinkedList<Target> pendingTargets;

    protected PendingMessageHandler(SessionImp session, int type) {
        HandlerThread thread = new HandlerThread("Message Handler Thread");
        thread.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        Looper looper = thread.getLooper();
        mHandler = new MHandler(looper);

        this.type = type;
        this.session = session;
        reportedState = session.getState();


        if (!isValidSessionState(reportedState)) {
            cancellAllOperations();
        }
    }

    protected abstract void handle(Message msg);


    protected void wrapAndSendMessage(Target target) {
        if(!TestPoke.isRunning()){
            TP.e("Operation couldn't be performed, TestPoke is not running");
            return;
        }

        if (session.wasCrashed()) {
            TP.e("Session was crashed, could not be possible to do something in that state");
            return;
        }

        if (SessionImp.STATE_NEW == reportedState) {
            if (null == pendingTargets)
                pendingTargets = new LinkedList<Target>();
            pendingTargets.offer(target);
            return;
        }

        sendMessageFor(target);
    }


    private void sendMessageFor(Target target) {
        Message msg = mHandler.obtainMessage(type, target);
        mHandler.sendMessage(msg);
    }


    protected final void flushPendingTargets() {
        if (null == pendingTargets)
            return;
        for (; ; ) {
            Target target = pendingTargets.poll();
            if (null == target)
                break;
            sendMessageFor(target);
        }
        pendingTargets = null;
    }

    protected final boolean isValidSessionState(int state) {
        return SessionImp.STATE_ABORTED != state && SessionImp.STATE_CLOSED != state && SessionImp.STATE_CRASHED != state;
    }

    protected final void cancellAllOperations() {
        TP.w("TestPoke will ignore next actions in previous session due on close/aborted state");
        operationsCancelled = true;
        destroy();
    }


    private void processSessionStateChange(int state) {
        if (SessionImp.STATE_NEW == state) {
            /**
             * Do nothing and return
             */
            return;
        }
        if (SessionImp.STATE_WEAK == state || SessionImp.STATE_OPEN == state) {
            operationsCancelled = false;
            flushPendingTargets();
        } else
            cancellAllOperations();
    }


    @Override
    public void onStateChange(int state) {
        this.reportedState = state;
        processSessionStateChange(state);
    }


    @Override
    public void destroy() {
        pendingTargets = null;
    }


    private class MHandler extends Handler {
        private MHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            handle(msg);
        }
    }
}
