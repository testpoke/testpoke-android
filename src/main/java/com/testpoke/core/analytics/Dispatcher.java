package com.testpoke.core.analytics;

import android.Manifest;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Process;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.testpoke.core.Injectable;
import com.testpoke.core.TPConfig;
import com.testpoke.core.content.$_V;
import com.testpoke.core.content.PersistenceProvider;
import com.testpoke.core.content.PersistenceResolver;
import com.testpoke.core.crashes.CrashData;
import com.testpoke.core.ia.$_U;
import com.testpoke.core.ia.Constants;
import com.testpoke.core.net.HttpRequest;
import com.testpoke.core.net.NetworkHelper;
import com.testpoke.core.net.StandardEndpoint;
import com.testpoke.core.util.Dump;
import com.testpoke.core.util.log.TP;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/*
 * Created by Jansel Valentin on 6/4/2014.
 */

final class Dispatcher implements SessionImp.StateReporter, Destroyable {

    private static Object endSessionSentinel = new Object();
    private static Object crashSessionSentinel = new Object();

    private int reportedState;

    private Context context;
    private DispatchWorker dispatchWorker;
    private CrashData crashData;
    private PersistenceResolver resolver;
    private ExecutorService executor;


    private CountDownLatch crashSyncLock = new CountDownLatch(1);

    private BlockingQueue<Object> sessions = new LinkedBlockingQueue<Object>();
    private ReentrantLock lock = new ReentrantLock();


    private Dispatcher() {
    }

    Dispatcher(Injectable<Context> caller) {
        InlineThread.checkCaller(caller);
        this.context = caller.get();
    }

    public void destroy() {
        if (null != executor)
            executor.shutdown();

        resolver = null;
        executor = null;
        dispatchWorker = null;
    }


    public int reportedState() {
        return reportedState;
    }

    void dispatch() {
        if( !NetworkHelper.isNetworkReady(context) )
            return;

        if (null != dispatchWorker && dispatchWorker.isRunning)
            return;

        if (SessionImp.STATE_CRASHED == reportedState)
            return;

        if (null == resolver)
            resolver = PersistenceProvider.getDefault(context).getResolver();
        /**
         * see * [Issue #14] - Unclosed sessions has been sent
         */
//        Cursor cursor = resolver.query($_V.V1.s, null, "uuid IS NOT '" + IA.k().uuid() + "'", null, null, null, null);
        Cursor cursor = resolver.query($_V.V1.s, null, "uuid IS NOT '" + IA.k().uuid() + "' AND end IS NOT NULL AND end_reason IS NOT NULL", null, null, null, null);

//        Cursor cursor = resolver.query($_V.V1.s, null, null, null, null, null, null);
        boolean sessionHasData = false;

        try {
            try {
                if (sessionHasData = cursor.moveToFirst()) {
                    startWorker();
                    do {
                        S s = parseCursorToS(cursor);
                        try {
                            sessions.put(s);
                        } catch (InterruptedException ex) {
                            Dump.printStackTraceCause(ex);
                        }
                    } while (cursor.moveToNext() && SessionImp.STATE_CRASHED != reportedState);
                }
            } catch (Exception ex) {
                Dump.printStackTraceCause(ex);
            }

            if (sessionHasData) {
                try {
                    sessions.put(endSessionSentinel);
                } catch (InterruptedException ex) {
                    Dump.printStackTraceCause(ex);
                }
            }
        }finally {
            if( null != cursor )
                cursor.close();
        }
    }

    private S parseCursorToS(Cursor cursor) {
        S s = new S();
        s.uuid = cursor.getString(1);
        s._ade677c68 = cursor.getString(2);
        s._ba8868af2 = cursor.getString(3);

        s.start = cursor.getString(4);
        s.start_reason = cursor.getString(5);
        s.end = cursor.getString(6);
        s.end_reason = cursor.getString(7);
        s.handled = cursor.getInt(8) == 1 ? true : false;
        s.zone = cursor.getString(9);
        return s;
    }

    private void startWorker() {
        if (null == dispatchWorker || !dispatchWorker.isRunning) {
            dispatchWorker = new DispatchWorker(this);
            dispatchWorker.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
            dispatchWorker.start();
        }
    }


    void dispatch(CrashData data) {
        if (null == resolver)
            resolver = PersistenceProvider.getDefault(context).getResolver();

        reportedState = SessionImp.STATE_CRASHED;
        SessionImp active = ((SessionImp) P.p().getActive(context));
        if (null == active)
            return;
        active.reportState(this);

        try {
            crashData = data;
            sessions.put(crashSessionSentinel);
            startWorker();

            crashSyncLock.await(10, TimeUnit.SECONDS);

        } catch (InterruptedException ex) {
            Dump.printStackTraceCause(ex);
        }
    }

    private String prepareSender(){
        String sessionSender = "unknown";

        if( null == context )
            return sessionSender;

        ContentProviderClient client = context.getContentResolver().acquireContentProviderClient(UsersIdentityContract.CONTENT_URI);
        if( null == client ){
            return sessionSender;
        }else{
            client.release();
            Cursor c = context.getContentResolver().query(UsersIdentityContract.CONTENT_URI,new String[]{UsersIdentityContract.COLUMN_IDENTIFIER}, UsersIdentityContract.COLUMN_LOGGED+"=?",new String[]{UsersIdentityContract.VALUE_LOGGED+""},null);
            try{
                if( c.moveToFirst() ) {
                    try {
                        sessionSender = c.getString(0);
                    }catch ( Exception ex ){}
                }
            }finally {
                if( null != c )
                    c.close();
            }
        }
        return sessionSender;
    }

    private void sendSession(S session) {
        if (null == resolver)
            resolver = PersistenceProvider.getDefault(context).getResolver();

        if (null == executor)
            return;

        if (null == session && reportedState == SessionImp.STATE_CRASHED) {
            Cursor cursor = resolver.query($_V.V1.s, null, " uuid='" + IA.k().uuid() + "' ", null, null, null, null);
            if (cursor.moveToFirst()) {
                session = parseCursorToS(cursor);
                session.crashed = true;
                cursor.close();
            }
        }
        executor.execute(new DispatchSessionSender(session));
    }


    private class DispatchSessionSender implements Runnable {
        public S s;
        private final int MAX_RETRY_INTENT = 2;
        private int currentRetry = 1;
        private boolean isDone;


        private DispatchSessionSender(S s) {
            this.s = s;
        }

        public void run() {

            if(!NetworkHelper.isNetworkReady(context)) {
                TP.w("Skip send sessions, device is not connected to network");
                if (reportedState == SessionImp.STATE_CRASHED) {
                    saveUnsentCrashData(s.uuid, crashData);
                    crashSyncLock.countDown();
                }
                return;
            }

            if (null == s || null == IA.k()._ade677c68() || skipThisSessionDueOnCrash(s))
                return;

            boolean isSessionCandidateToBeSent = false;
            try {
                JSONObject entity = new JSONObject();

                entity.put("uuid", s.uuid);
                entity.put("start", s.start);
                entity.put("start_reason", s.start_reason);
                entity.put("end", s.end);
                entity.put("end_reason", s.end_reason);
                entity.put("handled", s.handled);
                entity.put("zone",s.zone);

                entity.put(Constants._tec489d1d, Constants._s30450a25);

                JSONObject value = new JSONObject();
                entity.put("value", value);

                Charset charset = Charset.forName("UTF-8");
	 
                if (!Constants.production.equals(IA.k()._ea8268b38())) {
                    byte[][] logs = Collector.collectLogs(resolver, s.uuid);
                    if (null != logs && 0 < logs.length) {
                        isSessionCandidateToBeSent = true;

                        JSONArray jLogs = new JSONArray();
                        for (int i = 0; logs.length > i; ++i) {
                            String str = new String(logs[i],charset );
                            try {
                                jLogs.put( new JSONObject(str) );
                            }catch (Exception ex){
                                Dump.printStackTraceCause(ex);
                            }
                        }
                        value.put("logs", jLogs);

                    }
                }

                /**
                 * Collecting Session Crash
                 */
                byte[][] crashes = Collector.collectExceptions(resolver, s.uuid);
                if (null != crashes && 0 < crashes.length) {
                    isSessionCandidateToBeSent = true;

                    JSONArray jCrashes = new JSONArray();
                    for (int i = 0; crashes.length > i; ++i) {
                        String str = new String(crashes[i],charset );
                        try {
                            jCrashes.put( new JSONObject(str) );
                        }catch (Exception ex){
                            Dump.printStackTraceCause(ex);
                        }
                    }
                    value.put("ex", jCrashes);
                }


//                if (!Constants.production.equals(IA.k()._ea8268b38())) {
//                    if (BuildConfig.DEBUG)
//                        ML.e("Sending logcats to " + IA.k()._ea8268b38() + " environment for session " + s.uuid);
//
//                    if (TestPoke.getSettings().getOptions().reportLogcatAlerts()) {
//                        byte[][] alerts = Collector.collectAlertLogcat(resolver, s.uuid);
//                        if (null != alerts && 0 < alerts.length) {
//                            isSessionCandidateToBeSent = true;
//
//                            JSONArray jLca = new JSONArray();
//                            for (int i = 0; alerts.length > i; ++i) {
//                                String str = new String(alerts[i],charset );
//                                try {
//                                    jLca.put(new JSONObject(str));
//                                }catch (Exception ex){
//                                    Dump.printStackTraceCause(ex);
//                                }
//                            }
//
//                            value.put("lca", jLca);
//                        }
//                    }
//                }

                byte[][] events = Collector.collectEvents(resolver, s.uuid);
                if (null != events && 0 < events.length) {
                    isSessionCandidateToBeSent = true;

                    JSONArray jWorkflow = new JSONArray();
                    for (int i = 0; events.length > i; ++i) {
                        String str = new String(events[i],charset );
                        try {
                            jWorkflow.put( new JSONObject(str) );
                        }catch (Exception ex){
                            Dump.printStackTraceCause(ex);
                        }
                    }
                    value.put("workflow", jWorkflow );
                }


                if (s.crashed && null != crashData) {
                    JSONObject crash;
                    if (null == (crash = crashData.toJson()))
                        crash = new JSONObject();

                    value.put("crash", crash );
                    isSessionCandidateToBeSent = true;
                } else {
                    byte[] crash = Collector.collectCrash(resolver, s.uuid);
                    if (null != crash && 0 < crash.length) {
                        value.put("crash", new JSONObject(new String(crash)));
                        isSessionCandidateToBeSent = true;
                    }
                }


                /*
                    This code avoid orphan sessions were sent to testpoke
                    web, based on issue #17


                if (!isSessionCandidateToBeSent) {
                    TP.i("Skipping orphan session");
                    cleanNotActiveSession(resolver, s);
                    return;
                }*/

                // This is for always have one valid user, after user has logged in
                String sessionSender = prepareSender();
                entity.put("sender", sessionSender);


                JSONObject root = new JSONObject();

                JSONObject c = new JSONObject();
                c.put(Constants._s7768b7aa, IA.k()._s7768b7aa());

                root.put(Constants._ca0b77378, c);
                root.put("entity", entity);

                String json = root.toString();

                StandardEndpoint endpoint = StandardEndpoint.SESSION;
                try {
                    endpoint.parse(IA.k()._ade677c68(), s._ba8868af2);
                }catch (Exception ex ){
                    return;
                }
                
                final String agent = $_U.$4(context);
                final String a = $_U.$5() + (TextUtils.isEmpty(agent) ? $_U.$4() : agent);
                String _b = "";

                if ($_U.hasPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    _b = null == tm.getDeviceId() ? "" : tm.getDeviceId();
                }

                final String b = "["+ $_U.$4()+"]:["+_b+"]";

                if (NetworkHelper.isNetworkReady(context)) {
                    while (MAX_RETRY_INTENT >= currentRetry && !isDone && !skipThisSessionDueOnCrash(s)) {
                        boolean isRetrying = false;
                        try {

                            if( TPConfig.DEBUG )
                                TP.v( "Sending session "+s.uuid+" for bundle token "+s._ba8868af2  );


                            HttpResponse response = HttpRequest.executeHttpPost(endpoint, json.getBytes(charset), a,b);
                            int status = response.getStatusLine().getStatusCode();
                            if (200 == status) {
                                /**
                                 * Session was sent, cleaning not active session.
                                 */
                                isDone = true;
                                cleanNotActiveSession(resolver, s);
                            } else {
                            	currentRetry++;
                                TP.w("Error sending session" + s.uuid + ",, attempt #" + currentRetry + ",retrying in 2 seconds");
                            }
                        } catch (Exception ex) {
                            isRetrying = true;
                            currentRetry++;
                            TP.w("Error sending session" + s.uuid + ",, attempt #" + currentRetry + ",retrying in 2 seconds");
                        }
                        if (isRetrying) {
                            try {
                                TimeUnit.SECONDS.sleep(2);
                            } catch (InterruptedException ex) {
                                Dump.printStackTraceCause(ex);
                            }
                        }
                    }
                } else {
                    TP.w("Skipp send sessions, device is not connected to network");
                    if(  reportedState == SessionImp.STATE_CRASHED ){
                        saveUnsentCrashData(s.uuid,crashData);
                    }
                }
                if (!isDone) {
                    TP.w("Session " + s.uuid + ", could not be sent");
                }
                if (reportedState == SessionImp.STATE_CRASHED)
                    crashSyncLock.countDown();

            } catch (Exception ex) {
                Dump.printStackTraceCause(ex);
            }
        }


        private boolean skipThisSessionDueOnCrash(S s) {
            return SessionImp.STATE_CRASHED == reportedState && !s.crashed;
        }

        public void cleanNotActiveSession(PersistenceResolver resolver, S s) {
//            final String[] affectedTables = {$_V.V1.l, $_V.V1.a, $_V.V1.ev, $_V.V1.e, $_V.V1.c, $_V.V1.s};
            final String[] affectedTables = {$_V.V1.l, $_V.V1.ev, $_V.V1.e, $_V.V1.c};
            for (String table : affectedTables) {
                resolver.delete(table, " uuid='" + s.uuid + "' ", null);
            }

            if ( (!TextUtils.isEmpty(s.uuid) && s.uuid.equals( IA.k().uuid() )) && !s.crashed) {
                return;
            }

            if(TPConfig.DEBUG)
                TP.v( "Cleaning session "+s.uuid+" for bundle token "+s._ba8868af2  );


            resolver.delete($_V.V1.s, " uuid='" + s.uuid + "' ", null);
        }


        private void saveUnsentCrashData(String uuid, CrashData data) {
            if (null == uuid || null == data)
                return;
            ContentValues values = new ContentValues();
            values.put("uuid", uuid);
            values.put("pack", data.toByteArray());

            try {
                resolver.insert($_V.V1.c, "pack", values);
            } catch (Exception ex) {
                Dump.printStackTraceCause(ex);
            }
        }
    }


    private class DispatchWorker extends Thread {
        private BlockingQueue<Object> sessions;
        private Dispatcher dispatcher;
        private volatile boolean isRunning;

        DispatchWorker(Dispatcher dispatcher) {
            this.sessions = dispatcher.sessions;
            this.dispatcher = dispatcher;
        }


        @Override
        public void run() {
            isRunning = true;

            final int MAX_THREAD_NUMBER = 5;
            if (null == executor)
                executor = Executors.newFixedThreadPool(MAX_THREAD_NUMBER);

            for (; ; ) {
                if (Thread.currentThread().isInterrupted())
                    break;
                try {

                    final Object incoming = this.sessions.take();
                    boolean reportedCrash = this.sessions.contains(dispatcher.crashSessionSentinel);

                    if (incoming == dispatcher.endSessionSentinel && !reportedCrash)
                        break;

                    if (incoming != dispatcher.crashSessionSentinel && reportedCrash) {
                        continue;
                    } else if (incoming == dispatcher.crashSessionSentinel) {
                        dispatcher.sendSession(null);
                        break;
                    }

                    if (incoming instanceof S) {
                        dispatcher.sendSession((S) incoming);
                    }

                } catch (InterruptedException ex) {
                    Dump.printStackTraceCause(ex);
                    break;
                }
            }
            isRunning = false;
        }
    }

    private class S {
        String uuid;
        String _ade677c68;
        String _ba8868af2;
        String start;
        String start_reason;
        String end;
        String end_reason;
        String zone;
        boolean handled;
        boolean crashed;
    }
}
