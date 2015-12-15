package com.testpoke.core.analytics;

import android.content.ContentValues;
import android.os.Message;
import com.testpoke.api.ExceptionSender;
import com.testpoke.core.content.$_V;
import com.testpoke.core.content.PersistenceProvider;
import com.testpoke.core.content.PersistenceResolver;
import com.testpoke.core.net.encoding.json.JsonEncoder;
import com.testpoke.core.util.Dump;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * Created by Jansel Valentin on 6/24/2014.
 */
final class ExceptionSenderImp extends PendingMessageHandler<ExceptionSenderImp.PendingThrowable> implements ExceptionSender, SessionImp.StateListener {

    private static final int CRASH_SENDER_TYPE = 2;
    private static ExceptionSenderImp single;

    private Map<String, Object> cachedKeys;
    private ContentValues values;
    private JsonEncoder encoder;

    static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private Date fireAt;
    private Calendar calendar = GregorianCalendar.getInstance();

    ExceptionSenderImp(SessionImp session) {
        super(session, CRASH_SENDER_TYPE);
    }


    public static ExceptionSender getInstance(SessionImp session) {
        if (null != single)
            return single;
        synchronized (RemoteLoggerImp.class) {
            if (null == single) {
                single = new ExceptionSenderImp(session);
                session.registerStateListener(single);
            }
        }
        return single;
    }


    @Override
    public void send(Throwable thr) {
        wrapAndSendMessage(new PendingThrowable(thr));
    }


    @Override
    public void handle(Message msg) {
        if (CRASH_SENDER_TYPE == msg.what) {
            if (msg.obj instanceof PendingThrowable) {
                final PendingThrowable thr = (PendingThrowable) msg.obj;
                processThrowable(thr);
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }


    private void processThrowable(PendingThrowable thr) {
        if (operationsCancelled) {
            return;
        }

        PersistenceResolver resolver = PersistenceProvider.getDefault(session.getContext()).getResolver();
        if (null == values) {
            values = new ContentValues();
            encoder = JsonEncoder.getDefault();
            cachedKeys = new HashMap<String, Object>();
        }

        calendar.setTimeInMillis( System.currentTimeMillis() );
        fireAt = calendar.getTime();

        String formattedFiredAt;
        try{
            formattedFiredAt = format.format(fireAt);
        }catch( Exception e){
            formattedFiredAt = fireAt.toString();
        }

        cachedKeys.put("ex", Dump.getStacktraceLastThrowableCause(thr.tr));
        cachedKeys.put("fat", formattedFiredAt);

        byte[] out = null;
        try {
            out = encoder.encode(cachedKeys);
        } catch (IOException ex) {
            Dump.printStackTraceCause(ex);
        }
        values.put("uuid", IA.k().uuid());
        values.put("pack", out);
        resolver.insert($_V.V1.e, "pack", values);

        values.clear();
    }


    static class PendingThrowable {
        Throwable tr;
        public PendingThrowable(Throwable tr) {
            this.tr = tr;
        }
    }
}
