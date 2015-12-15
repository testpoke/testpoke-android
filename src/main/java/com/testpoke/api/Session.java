package com.testpoke.api;

/*
 * Created by Jansel Valentin on 5/25/14.
 */

public interface Session {

    String getId();

    boolean isOpen();

    RemoteLogger logger();

    ExceptionSender sendException(Throwable thr);

    Workflow sendEvent(String event);

    Workflow sendEvent(int level, String event);

    Workflow sendEvent(String category, String event);

    Workflow sendEvent(int level, String category, String event);
}
