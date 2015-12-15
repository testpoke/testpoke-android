package com.testpoke.core.analytics;

import com.testpoke.core.schedule.*;

/*
 * Created by Jansel Valentin on 5/5/14.
 */
final class SimpleSchedulerWrapper implements Scheduler {
    private static SimpleSchedulerWrapper singleInstance;

    private SchedulerService mBase;


    private SimpleSchedulerWrapper() {
        mBase = $SchedulerService$Holder.DEFAULT_SCHEDULER;
        $SchedulerService$Holder.DEFAULT_SCHEDULER = null;
    }

    static SimpleSchedulerWrapper shared() {
        if (null != singleInstance)
            return singleInstance;
        synchronized (SimpleSchedulerWrapper.class) {
            if (null == singleInstance) {
                singleInstance = new SimpleSchedulerWrapper();
            }
        }
        return singleInstance;
    }


    @Override
    public boolean schedule(Task task) {
        return mBase.schedule(task);
    }


    public void schedule(AutoDrivenTask task) {
        mBase.schedule(task);
    }


    @Override
    public boolean scheduleImmediate(Task task) {
        return mBase.scheduleImmediate(task);
    }


    @Override
    public void remove(Task task) {
        mBase.remove(task);
    }

    @Override
    public void start() {
        mBase.start();
    }

    @Override
    public void pause() {
        mBase.pause();
    }

    @Override
    public void stop() {
        mBase.stop();
    }

    @Override
    public void resume() {
        mBase.resume();
    }

    SchedulerService getSchedulerBase() {
        return mBase;
    }
}
