package com.testpoke.core.analytics;

import android.content.Context;
import com.testpoke.api.ExceptionSender;
import com.testpoke.api.RemoteLogger;
import com.testpoke.api.Workflow;
import com.testpoke.core.ia.$_T;

import java.util.*;

/*
 * Created by Jansel Valentin on 5/25/14.
 */

final class SessionImp implements com.testpoke.api.Session {

    static final int STATE_NEW = 1;
    static final int STATE_WEAK = 2;
    static final int STATE_OPEN = 3;
    static final int STATE_CLOSED = 4;
    static final int STATE_ABORTED = 5;
    static final int STATE_CRASHED = 6;


    private int state = STATE_NEW;

    private Date openAt;
    private Date closeAt;

    private Context context;


    private Calendar openCalendar = GregorianCalendar.getInstance();
    private Calendar closeCalendar = GregorianCalendar.getInstance();
    private List<StateListener> listeners;

    SessionImp(Context context) {
        this.context = context;

        if( null == IA.k()._ba8868af2() ) {
            IA.k()._ba8868af2($_T.$1(context));
        }
        listeners = new ArrayList<StateListener>();
    }

    SessionImp(Context context, int initialState) {
        this(context);
        this.state = initialState;
    }


    public String getId() {
        return STATE_WEAK == state ? state(state) : (STATE_NEW == state ? state(state) : IA.k().uuid());
    }

    @Override
    public boolean isOpen() {
        return STATE_OPEN == state || STATE_WEAK == state;
    }

    @Override
    public RemoteLogger logger() {
        return RemoteLoggerImp.getInstance(this);
    }

    @Override
    public ExceptionSender sendException(Throwable thr) {
        ExceptionSender sender = ExceptionSenderImp.getInstance(this);
        sender.send(thr);
        return sender;
    }


    public Workflow sendEvent(String label) {
        Workflow workflow = WorkflowImp.getInstance(this);
        workflow.sendEvent(label);
        return workflow;
    }



    public Workflow sendEvent(String category, String label) {
        Workflow workflow = WorkflowImp.getInstance(this);
        workflow.sendEvent(category, label);
        return workflow;
    }


    public Workflow sendEvent(int level, String label) {
        Workflow workflow = WorkflowImp.getInstance(this);
        workflow.sendEvent(level, label);
        return workflow;
    }


    public Workflow sendEvent(int level, String category, String label) {
        Workflow workflow = WorkflowImp.getInstance(this);
        workflow.sendEvent(level, category, label);
        return workflow;
    }

    Date getOpenTime() {
        if( null == openAt )
            touchOpenTime();
        return openAt;
    }

    Date getCloseTime() {
        if( null == closeAt )
            touchCloseTime();
        return closeAt;
    }

    void touchOpenTime() {
        openCalendar.setTimeInMillis(System.currentTimeMillis());
        openAt = openCalendar.getTime();
    }

    void touchCloseTime() {
        closeCalendar.setTimeInMillis( System.currentTimeMillis());
        closeAt = closeCalendar.getTime();
    }

    int getState() {
        return state;
    }

    boolean wasCrashed() {
        return STATE_CRASHED == state;
    }

    Context getContext() {
        return context;
    }

    void registerStateListener(StateListener listener) {
        if (null == listeners || listeners.contains(listener))
            return;
        listeners.add(listener);
    }


    void reportState(StateReporter reporter) {
        state = reporter.reportedState();
        notifyListeners(state);
    }

    void destroy() {
//        listeners.clear();
//        context = null;
    }

    private void notifyListeners(final int state) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = listeners.size() - 1; i >= 0; --i) {
                    StateListener listener = listeners.get(i);
                    listener.onStateChange(state);
                }
            }
        }).start();
    }

    static final String state(int state) {
        if (state == STATE_NEW)
            return "New";
        if (state == STATE_WEAK)
            return "Weak";
        if (state == STATE_OPEN)
            return "Open";
        if (state == STATE_ABORTED)
            return "Aborted";
        if (state == STATE_CLOSED)
            return "Closed";
        if (state == STATE_CRASHED)
            return "Crashed";
        return "UNKNOWN";
    }

    interface StateListener {
        void onStateChange(int state);
    }

    interface StateReporter {
        int reportedState();
    }
}
