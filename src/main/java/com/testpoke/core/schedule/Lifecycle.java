package com.testpoke.core.schedule;

/*
 * Created by Jansel Valentin on 5/5/14.
 */
public interface Lifecycle {

    void start();

    void pause();

    void stop();

    void resume();
}
