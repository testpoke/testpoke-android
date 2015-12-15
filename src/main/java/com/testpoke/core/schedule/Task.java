package com.testpoke.core.schedule;

/*
 * Created by Jansel Valentin on 5/5/14.
 */
public abstract class Task {
    private Scheduler scheduler;

    public abstract int getId();

    protected abstract void performTask();

    public Scheduler getScheduler(){
        return scheduler;
    }

    protected void setScheduler( Scheduler scheduler ){
        this.scheduler = scheduler;
    }

    final void run(){
        performTask();
    }
}
