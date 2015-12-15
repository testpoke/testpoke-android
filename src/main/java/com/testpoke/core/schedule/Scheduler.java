package com.testpoke.core.schedule;

/*
 * Created by Jansel Valentin on 5/5/14.
 */
public interface Scheduler extends Lifecycle {

    boolean schedule(Task task);

    boolean scheduleImmediate(Task task);

    void remove(Task task);

}
