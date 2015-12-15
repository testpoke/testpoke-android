package com.testpoke.core.schedule;

import android.os.Handler;
import android.os.Message;

/*
 * Created by Jansel Valentin on 5/5/14.
 */
final class SchedulerHandler extends Handler {

    private SchedulerService scheduler;
    public SchedulerHandler( SchedulerService scheduler ){
        this.scheduler = scheduler;
    }

    @Override
    public void handleMessage(Message msg) {
        if (this != msg.getTarget() || !(msg.obj instanceof Task))
            return;

        final Task task = (Task) msg.obj;
        task.run();

        if (task instanceof Recurrent) {
            Recurrent rTask = (Recurrent) task;
            long recurrent = rTask.getRecurrentInterval();
            recurrent = recurrent > 0 ? recurrent : 0;

            if (0 < recurrent) {

                if( task instanceof AutoDrivenTask){
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scheduler.schedule( (AutoDrivenTask)task );
                        }
                    }, recurrent );
                }else{
                    Message rMsg = obtainMessage(task.getId(), task);
                    sendMessageDelayed(rMsg, recurrent);
                }
            }
        }
    }
}
