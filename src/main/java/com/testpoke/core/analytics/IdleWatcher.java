package com.testpoke.core.analytics;

import android.content.Context;
import com.testpoke.TestPoke;
import com.testpoke.core.schedule.Recurrent;
import com.testpoke.core.schedule.SchedulerService;
import com.testpoke.core.schedule.Task;
import com.testpoke.core.util.Tasks;

/*
 * Created by Jansel Valentin on 5/24/14.
 */

/*package*/ final class IdleWatcher extends Task implements Recurrent {

    private static final long DEFAULT_RETRY_BACKGROUND = 2000;
    private static final long DEFAULT_RETRY_FOREGROUND = 1000;
    private static final int  LIMIT_LAZY_LATEST_SAVE_PERIOD = 30;

    private Context context;

    private long idleTime;
    private volatile long recurrentInterval = DEFAULT_RETRY_BACKGROUND;
    private int openCloseReason = OCReason.FOREGROUND;
    private boolean isSessionAutoHandled;
    private boolean isMonitored;
    private int lazyLatestSaveCounter;

    private Object lock;


    public IdleWatcher(Context context, boolean isMonitored) {
        this.context = context;
        lock = new Object();
        this.isMonitored = isMonitored;
        isSessionAutoHandled = TestPoke.getSettings().getOptions().isSessionAutoHandled();
    }


    @Override
    public long getRecurrentInterval() {
        return recurrentInterval;
    }

    @Override
    public int getId() {
        return Tasks.IDLE_WATCHER;
    }

    @Override
    protected void performTask() {

        if (0 == lazyLatestSaveCounter && isSessionAutoHandled)
            Latest.save(context);

        if (LIMIT_LAZY_LATEST_SAVE_PERIOD <= lazyLatestSaveCounter++)
            lazyLatestSaveCounter = 0;

        synchronized (lock) {
            if (0 == recurrentInterval)
                return;
        }


        SessionImp active = ((SessionImp) P.p().getActive(context));

        if (isSessionAutoHandled && null != active && !active.wasCrashed() && active.isOpen()) {

            if (SessionUtils.isInForeground(context)) {

                recurrentInterval = isMonitored ? 0 : DEFAULT_RETRY_FOREGROUND;
//                recurrentInterval =  DEFAULT_RETRY_FOREGROUND;
                idleTime = 0L;
                openCloseReason = OCReason.FOREGROUND;

            } else if (OCReason.BACKGROUND != openCloseReason) {
                recurrentInterval = DEFAULT_RETRY_BACKGROUND;


                long current = System.currentTimeMillis();
                if (0 == idleTime)

                    idleTime = current;

                long elapsed = current - idleTime;
                long defaultTimeout = 0L;
                if (OCReason.FOREGROUND == openCloseReason)
                    defaultTimeout = TestPoke.getSettings().getOptions().sessionTimeout();

                if (defaultTimeout <= elapsed) {
                    idleTime = 0;

                    openCloseReason = OCReason.BACKGROUND;
                    SessionUtils.closeSession(context, (SchedulerService) getScheduler(), openCloseReason);
                    SessionUtils.openSession(context, (SchedulerService) getScheduler(), openCloseReason);
                }
            }
        } else {
            recurrentInterval = 0;
        }
    }

    void reloadInterval() {
        recurrentInterval = DEFAULT_RETRY_BACKGROUND;
    }

    void stopWatch() {
    /*
        Issue 2 ya no va y el lock ya no se necessiaria en vista de que ya la session no se cerraria por esta via.
     */
        synchronized (lock) {
            recurrentInterval = 0;
            SessionUtils.closeSession(context, (SchedulerService) getScheduler(), openCloseReason);
        }
    }
}
