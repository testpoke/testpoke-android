package com.testpoke.core.schedule.event;

import android.content.Context;
import android.os.Build;
import android.os.Looper;
import com.testpoke.core.schedule.SchedulerService;
import com.testpoke.core.util.Tasks;

/*
 * Created by Jansel Valentin on 5/6/14.
 */
public final class Termination extends TaskEvent {

    public Termination(SchedulerService scheduler, Context context ){
        super(scheduler,context);
    }

    @Override
    public int getId() {
        return Tasks.TERMINATION;
    }

    @Override
    protected void performTask() {
        quitSafely();
    }

    private void quitSafely() {

        final Looper myLooper = Looper.myLooper();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            myLooper.quitSafely();
        } else {
            myLooper.quit();
        }
    }
}
