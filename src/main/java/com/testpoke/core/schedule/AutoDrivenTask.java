package com.testpoke.core.schedule;

/*
 * Created by Jansel Valentin on 5/5/14.
 */
public abstract class AutoDrivenTask extends Task {

    protected void setScheduler(SchedulerService scheduler) {
        super.setScheduler(scheduler);
    }
}
