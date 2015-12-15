package com.testpoke.api;

/*
 * Created by Jansel Valentin on 09/05/14.
 */
public interface  Workflow {

    void sendEvent(String label);

    void sendEvent(int level, String label);

    void sendEvent(String category, String label);

    void sendEvent(int level, String category, String label);
}

