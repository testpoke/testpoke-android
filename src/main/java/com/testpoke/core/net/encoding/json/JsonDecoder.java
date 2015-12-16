package com.testpoke.core.net.encoding.json;

import com.testpoke.core.net.encoding.Decoder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by Jansel Valentin on 12/15/2014.
 */
public final class JsonDecoder implements Decoder<Map<String, Object>, byte[]> {

    private static final JsonDecoder DEFAULT = new JsonDecoder();

    private JsonDecoder() {
    }

    public static JsonDecoder getDefault() {
        return DEFAULT;
    }

    @Override
    public Map<String, Object> decode(byte[] bytes) throws IOException {
        if( null == bytes || 0 == bytes.length )
            return Collections.emptyMap();

        Map<String,Object> result;

        try {
            JSONObject json = new JSONObject(new String(bytes, Charset.forName("UTF-8")));
            result = toMap(json);
        }catch ( JSONException ex ){
             throw new IOException(ex);
        }
        return result;
    }


    public static Map toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}
