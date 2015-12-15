package com.testpoke.core.schedule.event;

import android.content.Context;
import com.testpoke.core.schedule.SchedulerService;
import com.testpoke.core.schedule.Task;

/*
 * Created by Jansel Valentin on 5/23/14.
 */
public abstract class TaskEvent extends Task implements Event{
    private Context context;

    protected TaskEvent( SchedulerService schedulerService, Context context ){
        this.context = context;
        setScheduler(schedulerService);
    }

    protected Context getContext(){ return context; }

    public void fire(){
        getScheduler().schedule(this);
    }

    public void fireImmediate(){
        getScheduler().scheduleImmediate(this);
    }
}
