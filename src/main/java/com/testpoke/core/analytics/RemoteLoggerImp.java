package com.testpoke.core.analytics;

import android.content.ContentValues;
import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import com.testpoke.api.RemoteLogger;
import com.testpoke.core.content.$_V;
import com.testpoke.core.content.PersistenceProvider;
import com.testpoke.core.content.PersistenceResolver;
import com.testpoke.core.net.encoding.json.JsonEncoder;
import com.testpoke.core.util.Dump;
import com.testpoke.core.util.log.TP;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * Created by Jansel Valentin on 6/9/2014.
 */
final class RemoteLoggerImp extends PendingMessageHandler<RemoteLoggerImp.PendingLog> implements RemoteLogger {

    private static final int VERBOSE = 2;
    private static final int DEBUG = 3;
    private static final int INFO = 4;
    private static final int WARN = 5;
    private static final int ERROR = 6;

    private static final int REMOTE_LOGGER_TYPE = 1;

    private static final int MAX_MSG_LENGTH = 160;

    private static RemoteLoggerImp single;

    private String defaultTag;

    private Map<String, Object> cachedKeys;
    private ContentValues values;
    private JsonEncoder encoder;


    private Date createdAt;
    private Calendar calendar = GregorianCalendar.getInstance();
    static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");


    private RemoteLoggerImp(SessionImp session) {
        super(session, REMOTE_LOGGER_TYPE);
        defaultTag = getDefaultTag(session.getContext());
    }


    static RemoteLogger getInstance(SessionImp session) {
        if (null != single)
            return single;
        synchronized (RemoteLoggerImp.class) {
            if (null == single) {
                single = new RemoteLoggerImp(session);
                session.registerStateListener(single);
            }
        }
        return single;
    }


    @Override
    public void d(String msg) {
        log(DEBUG, defaultTag, msg);
    }


    @Override
    public void d(String tag, String msg) {
        log(DEBUG, tag, msg);
    }

    @Override
    public void e(String msg) {
        log(ERROR, defaultTag, msg);
    }

    @Override
    public void e(String tag, String msg) {
        log(ERROR, tag, msg);
    }

    @Override
    public void i(String msg) {
        log(INFO, defaultTag, msg);
    }

    @Override
    public void i(String tag, String msg) {
        log(INFO, tag, msg);
    }

    @Override
    public void v(String msg) {
        log(VERBOSE, defaultTag, msg);
    }

    @Override
    public void v(String tag, String msg) {
        log(VERBOSE, tag, msg);
    }

    @Override
    public void w(String msg) {
        log(WARN, defaultTag, msg);
    }

    @Override
    public void w(String tag, String msg) {
        log(WARN, tag, msg);
    }


    @Override
    public void destroy() {
        super.destroy();
    }


    @Override
    protected void handle(Message msg) {
        if (REMOTE_LOGGER_TYPE == msg.what) {
            if (msg.obj instanceof PendingLog) {
                final PendingLog log = (PendingLog) msg.obj;
                processLog(log);
            }
        }
    }


    private void log(int level, String tag, String msg) {
        wrapAndSendMessage(new PendingLog(level, tag, msg));
    }


    private String getDefaultTag(Context context) {
        CharSequence label = context.getApplicationInfo().loadLabel(context.getPackageManager());
        if (TextUtils.isEmpty(label))
            label = context.getPackageName();
        return null == label ? "<tag>" : label.toString();
    }


    private String getNormalizedMessage(String msg) {
        if (!TextUtils.isEmpty(msg) && MAX_MSG_LENGTH < msg.length()) {
            TP.w("Maximum log message length is 160,the rest of the message is ignored");
        }
        return null == msg ? "" : MAX_MSG_LENGTH < msg.length() ? msg.substring(0, MAX_MSG_LENGTH) : msg;
    }

    private void processLog(PendingLog log) {
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
        createdAt = calendar.getTime();

        String formattedCreatedAt;
        try{
            formattedCreatedAt = format.format(createdAt);
        }catch( Exception e){
            formattedCreatedAt = createdAt.toString();
        }
        cachedKeys.put("level", log.level);
        cachedKeys.put("tag", TextUtils.isEmpty(log.tag) ? defaultTag : log.tag);
        cachedKeys.put("msg", getNormalizedMessage(log.msg));
        cachedKeys.put("cat", formattedCreatedAt);

        byte[] out = null;
        try {
            out = encoder.encode(cachedKeys);
        } catch (IOException ex) {
            Dump.printStackTraceCause(ex);
        }
        if (null == out || 0 >= out.length)
            return;
        values.put("uuid", IA.k().uuid());
        values.put("pack", out);
        resolver.insert($_V.V1.l, "pack", values);

        values.clear();
    }


    static class PendingLog {
        final int level;
        final String tag;
        final String msg;

        PendingLog(int level, String tag, String msg) {
            this.level = level;
            this.tag = tag;
            this.msg = msg;
        }
    }
}
