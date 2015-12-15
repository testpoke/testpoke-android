package com.testpoke.core.analytics;

import android.content.ContentValues;
import android.os.Message;
import com.testpoke.api.Workflow;
import com.testpoke.core.content.$_V;
import com.testpoke.core.content.PersistenceProvider;
import com.testpoke.core.content.PersistenceResolver;
import com.testpoke.core.net.encoding.json.JsonEncoder;
import com.testpoke.core.util.Dump;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

final class WorkflowImp extends PendingMessageHandler<WorkflowImp.PendingEvent> implements Workflow {

    private static WorkflowImp single;

    private static final int WORKFLOW_TYPE = 3;

    private static final AtomicInteger xOrder = new AtomicInteger(1);

    private static final int UNDEFINED_LEVEL = -1;
    private static final String UNDEFINED_CATEGORY = "undefined";

    private Calendar calendar = GregorianCalendar.getInstance();
    static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private Map<String, Object> cachedKeys;
    private ContentValues values;
    private JsonEncoder encoder;

    public static Workflow getInstance(SessionImp session) {
        if (null != single)
            return single;
        synchronized (RemoteLoggerImp.class) {
            if (null == single) {
                single = new WorkflowImp(session, WORKFLOW_TYPE);
                session.registerStateListener(single);
            }
        }
        return single;
    }


    WorkflowImp(SessionImp session, int type) {
        super(session, type);
    }


    @Override
    public void sendEvent(String label) {
        sendEvent(new PendingEvent(UNDEFINED_LEVEL,xOrder.getAndIncrement(), UNDEFINED_CATEGORY, label));
    }

    @Override
    public void sendEvent(int level, String label) {
        sendEvent(new PendingEvent(level, xOrder.getAndIncrement(),UNDEFINED_CATEGORY, label));
    }

    @Override
    public void sendEvent(String category, String label) {
        sendEvent(new PendingEvent(UNDEFINED_LEVEL,xOrder.getAndIncrement(), category, label));
    }

    @Override
    public void sendEvent(int level, String category, String label) {
        sendEvent(new PendingEvent(level, xOrder.getAndIncrement(),category, label));
    }

    @Override
    public void destroy() {
        super.destroy();

    }

    @Override
    public void onStateChange(int state) {
        this.reportedState = state;
        if (SessionImp.STATE_NEW == state) {
            return;
        }
        if (SessionImp.STATE_WEAK == state || SessionImp.STATE_OPEN == state || SessionImp.STATE_CRASHED == state ) {
            flushPendingTargets();
        } else
            cancellAllOperations();
    }


    @Override
    protected void handle(Message msg) {
        if (WORKFLOW_TYPE == msg.what) {
            if (msg.obj instanceof PendingEvent) {
                final PendingEvent event = (PendingEvent) msg.obj;
                processEvent(event);
            }
        }
    }


    private void sendEvent( PendingEvent event ){
        wrapAndSendMessage( event );
    }


    private void processEvent(PendingEvent event) {
        if (operationsCancelled) {
            return;
        }

        PersistenceResolver resolver = PersistenceProvider.getDefault(session.getContext()).getResolver();
        if (null == values) {
            values = new ContentValues();
            encoder = JsonEncoder.getDefault();
            cachedKeys = new HashMap<String, Object>();
        }


        cachedKeys.put("order", event.order);
        cachedKeys.put("level", event.level);
        cachedKeys.put("time", event.time);
        cachedKeys.put("category", event.category);
        cachedKeys.put("label", event.label);

        if (session.wasCrashed()) {
            cachedKeys.put("crashed", true);
        }

        byte[] out = null;
        try {
            out = encoder.encode(cachedKeys);
        } catch (IOException ex) {
            Dump.printStackTraceCause(ex);
        }
        values.put("uuid", IA.k().uuid());
        values.put("pack", out);
        resolver.insert($_V.V1.ev, "pack", values);

        values.clear();
    }


    class PendingEvent {
        final int order;
        final int level;
        final String category;
        final String label;
        final String time;


        PendingEvent(int level,int order, String category, String label) {
            this.order = order;
            this.level = level;
            this.category = category;
            this.label = label;
            calendar.setTimeInMillis(System.currentTimeMillis());

            String formattedTime;
            try{
                formattedTime = format.format( calendar.getTime() );
            }catch( Exception e) {
                formattedTime = calendar.getTime().toString();
            }
            time = formattedTime;
        }
    }
}
