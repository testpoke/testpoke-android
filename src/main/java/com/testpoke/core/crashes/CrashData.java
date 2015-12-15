package com.testpoke.core.crashes;

import org.json.JSONObject;

/*
 * Created by Jansel Valentin on 5/20/14.
 */
public interface CrashData {
    byte[] toByteArray();

   JSONObject toJson();
}
