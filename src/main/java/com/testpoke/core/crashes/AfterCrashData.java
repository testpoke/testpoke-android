package com.testpoke.core.crashes;

import android.text.format.Time;
import com.testpoke.core.net.encoding.json.JsonEncoder;
import com.testpoke.core.util.Dump;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/*
 * Created by Jansel Valentin on 5/20/14.
 */
/*package*/ class AfterCrashData implements CrashData {

    private static final String FIELD_CRASH_TIME = "ct";
    private static final String FIELD_STACKTRACE = "st";
    private static final String FIELD_CONFIGURATION = "config";
    private static final String FIELD_SHARED_PREFERENCES = "sp";
    private static final String FIELD_DEVICE_STATE = "dev_state";

    private Map<String, Object> fields;

    AfterCrashData() {
        fields = new HashMap<String, Object>();
    }

    void setCrashTime(Time crashTime) {
        fields.put(FIELD_CRASH_TIME, crashTime.format3339(false));
    }

    void setStackTrace(String stackTrace) {
        fields.put(FIELD_STACKTRACE, stackTrace);
    }

    void setConfiguration(Map<String,String> configuration) {
        fields.put(FIELD_CONFIGURATION, configuration);
    }

    void setSharedPreferences(Map<String,Object> sharedPreferences) {
        fields.put(FIELD_SHARED_PREFERENCES, sharedPreferences);
    }

    void setDeviceState(Map<String,Object> deviceState ){
        fields.put(FIELD_DEVICE_STATE,deviceState);
    }

    @Override
    public byte[] toByteArray() {
        JsonEncoder encoder = JsonEncoder.getDefault();
        try {
            return encoder.encode(fields);
        } catch (Exception ex) {
            Dump.printStackTraceCause(ex);
        }
        return new byte[0];
    }

    @Override
    public JSONObject toJson() {
        try {
            return (JSONObject) JsonEncoder.coerceToJson(fields);
        }catch ( Exception ex ){
        }
        return new JSONObject();
    }
}

