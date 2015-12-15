package com.testpoke.core.analytics;

import android.app.ActivityManager;
import android.content.Context;
import com.testpoke.core.schedule.SchedulerService;
import com.testpoke.core.schedule.event.Events;

import java.util.List;

/*
 * Created by Jansel Valentin on 5/25/14.
 */
final class SessionUtils {

    static void openSession(Context context, SchedulerService scheduler, int reason) {
        if (-1 == reason)
            reason = SessionUtils.isInForeground(context) ? OCReason.FOREGROUND : OCReason.BACKGROUND;

        OpenSession os = Events.prepare(OpenSession.class, scheduler, context);
        os.setStartReason(reason);
        os.fire();
    }

    static void closeSession(Context context, SchedulerService scheduler, int reason) {
        if (-1 == reason)
            reason = SessionUtils.isInForeground(context) ? OCReason.FOREGROUND : OCReason.BACKGROUND;

        CloseSession cs = Events.prepare(CloseSession.class, scheduler, context);
        cs.setEndReason(reason);
        cs.fire();
    }

    static boolean isInForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        if (null == processes)
            return false;
        for (int i = 0; processes.size() > i; ++i) {
            ActivityManager.RunningAppProcessInfo process = processes.get(i);
            if (android.os.Process.myPid() == process.pid)
                return ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND == process.importance;
        }
        return false;
    }

}
