package com.testpoke.core.schedule;

import android.os.Looper;
import android.os.Message;
import com.testpoke.core.schedule.event.Events;
import com.testpoke.core.schedule.event.Termination;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 * Created by Jansel Valentin on 5/5/14.
 */
public final class SchedulerService implements Scheduler {

    private AtomicBoolean isCreatedNotStarted;
    private AtomicBoolean isStarted;

    private LinkedList<PendingTask> pendingTasks;

    private SchedulerHandler mHandler;
    private Lock lock;

    SchedulerService() {
        isCreatedNotStarted = new AtomicBoolean(true);
        isStarted = new AtomicBoolean(false);
        lock = new ReentrantLock();
    }


    @Override
    public boolean schedule(Task task) {
        return schedule(task, false);
    }

    public void schedule(AutoDrivenTask task) {
        task.setScheduler(this);
    }

    @Override
    public boolean scheduleImmediate(Task task) {
        return schedule(task, true);
    }

    private boolean schedule(Task task, boolean immediate) {
        if( isCreatedNotStarted.get() ){
            if( null == pendingTasks )
                pendingTasks = new LinkedList<PendingTask>();

            pendingTasks.offer( new PendingTask(task,immediate));
            return true;
        }

        if (!isStarted.get())
            return false;

        boolean scheduled = false;
        try {
            lock.lock();

            remove(task);

            if (this != task.getScheduler())
                task.setScheduler(this);
            Message msg = mHandler.obtainMessage(task.getId(), task);

            if (immediate)
                scheduled = mHandler.sendMessageAtFrontOfQueue(msg);
            else
                scheduled = mHandler.sendMessage(msg);
        } finally {
            lock.unlock();
        }
        return scheduled;
    }


    @Override
    public void remove(Task task) {
        try {
            lock.lock();
            mHandler.removeMessages(task.getId());
        } finally {
            lock.unlock();
        }
    }


    @Override
    public void start() {
        if (isStarted.get())
            return;

        lock.lock();
        if (null != mHandler)
            return;

        final Condition condition = lock.newCondition();
        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    try{
                        lock.lock();
                        mHandler = new SchedulerHandler(SchedulerService.this);
                        condition.signalAll();
                    }finally {
                        lock.unlock();
                    }
                    Looper.loop();
                }
            });
            t.start();


            while (null == mHandler)
                condition.awaitUninterruptibly();
            isStarted.set(true);

        } finally {
            lock.unlock();
        }

        if( isCreatedNotStarted.get() ){
            isCreatedNotStarted.set(false);
            flushPendingTasks();
            pendingTasks = null;
        }
    }

    @Override
    public void stop() {
        Events.prepare(Termination.class, this, null).fire();
        isStarted.set(false);
    }

    @Override
    public void resume() {
    }
    @Override
    public void pause() {
    }

    private void flushPendingTasks(){

        if( null == pendingTasks )
            return;
        PendingTask pendingTask;
        for(;;){
            pendingTask= pendingTasks.poll();
            if( null == pendingTask )
                break;
            schedule(pendingTask.task,pendingTask.immediate);
        }
    }

    private class PendingTask{
        final Task task;
        final boolean immediate;
        PendingTask( Task task, boolean immediate ){
            this.task = task;
            this.immediate = immediate;
        }
    }
}


