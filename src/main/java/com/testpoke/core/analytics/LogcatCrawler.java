package com.testpoke.core.analytics;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;
import com.testpoke.core.content.PersistenceProvider;
import com.testpoke.core.content.PersistenceResolver;
import com.testpoke.core.net.encoding.json.JsonEncoder;
import com.testpoke.core.schedule.Task;
import com.testpoke.core.util.BoundedLinkedList;
import com.testpoke.core.util.Dump;
import com.testpoke.core.util.Tasks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
final class LogcatCrawler extends Task {

    private static final String RECENT_LOGCAT_TIME = "com.testpoke.RECENT_LOGCAT_TIME";
    private static final String RECENT_LOGCAT_TIME_KEY = "time";

    private static LogcatCrawler single;

    private String uuid;
    private Context context;
    private PersistenceResolver resolver;
    private Map<String, Object> cachedKeys;
    private ContentValues values;

    private LogcatCrawler(Context context) {
        this.context = context;
    }

    public static LogcatCrawler getInstance(Context context) {
        if (null != single)
            return single;
        synchronized (LogcatCrawler.class) {
            if (null == single) {
                single = new LogcatCrawler(context);
            }
        }
        return single;
    }

    @Override
    public int getId() {
        return Tasks.LOGCAT_CRAWLER;
    }

    @Override
    protected void performTask() {
        final List<String> alerts = crawlLogcatAlerts(context);

        if (0 >= alerts.size() || null == context || TextUtils.isEmpty(uuid))
            return;

        if (null == cachedKeys) {
            cachedKeys = new HashMap<String, Object>();
            values = new ContentValues();
            resolver = PersistenceProvider.getDefault(context).getResolver();
        }

        for (int i = 0; alerts.size() > i; ++i) {
            String alert = alerts.get(i);

            if (TextUtils.isEmpty(alert))
                continue;

            cachedKeys.put("uuid", uuid);
            cachedKeys.put("alert", alerts.get(i));

            byte[] data = null;
            try {
                data = JsonEncoder.getDefault().encode(cachedKeys);
            } catch (Exception ex) {
                Dump.printStackTraceCause(ex);
            }
            if (null == data || 0 >= data.length)
                continue;

            values.put("uuid", uuid);
            values.put("pack", data);
//            resolver.insert($_V.V1.a, "pack", values);
            values.clear();
        }
    }


    void setReleatedSession(String uuid) {
        this.uuid = uuid;
    }

    private  List<String> crawlLogcatAlerts(Context context) {

        final BoundedLinkedList<String> exportedLines = new BoundedLinkedList<String>(20);

        final int myPid = android.os.Process.myPid();
        final String strMyPid = myPid + "):";

        Time mostRecent = null;
        final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        try {
            mostRecent = new Time(timeFormat.parse(loadMostRecentTime(context)).getTime());
        } catch (Exception ex) {
            Dump.printStackTraceCause(ex);
        }

        try {
            final Process proc = Runtime.getRuntime().exec("logcat -v time -t 2000");

            new Thread(new Runnable() {
                public void run() {
                    final InputStream in = proc.getErrorStream();
                    byte[] fake = new byte[8192];
                    try {
                        while (in.read(fake) >= 0) ;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();

            Time recentLoadedLine = null;
            Pattern pattern = Pattern.compile("\\b(\\d){2}:(\\d){2}:(\\d){2}(?=\\.)");

            final BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;

            while (null != (line = reader.readLine())) {
                if (!line.contains(strMyPid)) {
                    continue;
                }

                if (isAcceptableLine(line)) {
                    boolean allowFilter = true;
                    try {
                        String timeFragment;
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.find()) {
                            timeFragment = matcher.group(0);
                            recentLoadedLine = new Time(timeFormat.parse(timeFragment).getTime());
                            if (null == mostRecent)
                                mostRecent = recentLoadedLine;
                        } else {
                            allowFilter = false;
                        }
                    } catch (Exception ex) {
                        allowFilter = false;
                    }

                    if (allowFilter) {
                        if (recentLoadedLine.getTime() > mostRecent.getTime())
                            exportedLines.add(line);
                    } else {
                        exportedLines.add(line);
                    }
                }
            }
            mostRecent = recentLoadedLine;

            if (null != mostRecent)
                saveMostRecentTime(context, mostRecent.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return exportedLines;
    }

    private boolean isAcceptableLine(String line) {
        boolean acceptable = true;
        acceptable &= line.contains("Choreographer")
                   || line.contains("GC");
        return acceptable;
    }

    private void saveMostRecentTime(Context context, String time) {
        context.getSharedPreferences(RECENT_LOGCAT_TIME, Context.MODE_PRIVATE)
                .edit()
                .putString(RECENT_LOGCAT_TIME_KEY, time)
                .commit();
    }

    private String loadMostRecentTime(Context context) {
        return context.getSharedPreferences(RECENT_LOGCAT_TIME, Context.MODE_PRIVATE).getString(RECENT_LOGCAT_TIME_KEY, "00:00:00");
    }
}