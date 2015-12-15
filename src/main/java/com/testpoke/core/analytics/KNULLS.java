package com.testpoke.core.analytics;

import com.testpoke.api.ExceptionSender;
import com.testpoke.api.RemoteLogger;
import com.testpoke.api.Session;
import com.testpoke.api.Workflow;
import com.testpoke.core.util.log.TP;

/**
 * Created by Jansel Valentin on 10/20/2014.
 *
 * Null representation for Session
 */
public class KNULLS implements Session {

    public static final KNULLS _N = new KNULLS();

    private NullComponent NULL = new NullComponent();

    private KNULLS(){
    }

    private void reportNotActiveSession(){
        TP.e("This operation couldn't be performed, not active session or TestPoke is not running");
    }

    @Override
    public String getId() {
        return "<not active session>";
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public RemoteLogger logger() {
        reportNotActiveSession();
        return NULL;
    }

    @Override
    public ExceptionSender sendException(Throwable thr) {
        reportNotActiveSession();
        return NULL;
    }

    @Override
    public Workflow sendEvent(String label) {
        reportNotActiveSession();
        return NULL;
    }

    public Workflow sendEvent(int level, String label) {
        reportNotActiveSession();
        return NULL;
    }

    public Workflow sendEvent(String category, String label) {
        reportNotActiveSession();
        return NULL;
    }

    @Override
    public Workflow sendEvent(int level, String category, String label) {
        reportNotActiveSession();
        return NULL;
    }

    class NullComponent implements RemoteLogger, Workflow, ExceptionSender {
        @Override
        public void send(Throwable thr) {
            reportNotActiveSession();
        }
        @Override
        public void d(String msg) {
            reportNotActiveSession();
        }
        @Override
        public void d(String tag, String msg) {
            reportNotActiveSession();
        }
        @Override
        public void e(String msg) {
            reportNotActiveSession();
        }
        @Override
        public void e(String tag, String msg) {
            reportNotActiveSession();
        }
        @Override
        public void i(String msg) {
            reportNotActiveSession();
        }
        @Override
        public void i(String tag, String msg) {
            reportNotActiveSession();
        }
        @Override
        public void v(String msg) {
            reportNotActiveSession();
        }
        @Override
        public void v(String tag, String msg) {
            reportNotActiveSession();
        }
        @Override
        public void w(String msg) {
            reportNotActiveSession();
        }
        @Override
        public void w(String tag, String msg) {
            reportNotActiveSession();
        }
        @Override
        public void sendEvent(String label) {
            reportNotActiveSession();
        }
        @Override
        public void sendEvent(int level, String label) {
            reportNotActiveSession();
        }
        @Override
        public void sendEvent(String category, String label) {
            reportNotActiveSession();
        }
        @Override
        public void sendEvent(int level, String category, String label) {
            reportNotActiveSession();
        }
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof KNULLS && ((KNULLS)o) == _N);
    }
}
