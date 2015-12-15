package com.testpoke.core.net.encoding.json;

import com.testpoke.core.net.encoding.Encoder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Created by Jansel Valentin on 12/15/2014.
 */
public final class JsonEncoder implements Encoder<byte[], Map<String, Object>> {
    private static final JsonEncoder DEFAULT = new JsonEncoder();

    private JsonEncoder() {
    }

    public static JsonEncoder getDefault() {
        return DEFAULT;
    }

    @Override
    public byte[] encode(Map<String, Object> objectMap ) throws IOException {
        JSONObject json;
        try {
            json = (JSONObject)coerceToJson(objectMap);
        } catch (JSONException e) {
            throw new IOException( e );
        }
        return json.toString().getBytes(Charset.forName("UTF-8"));
    }


    public static Object coerceToJson(Object object) throws JSONException {
        if( object instanceof Map ){
            JSONObject json = new JSONObject();

            Map<String,?> map = ( Map<String,?> ) object;
            for( String key : map.keySet() ){
                json.put(key,coerceToJson(map.get(key)));
            }
            return  json;
        }

        if( object instanceof List){
            JSONArray array = new JSONArray();
            List list = (List) object;
            for (int i = 0; i <list.size(); i++) {
                array.put( coerceToJson(list.get(i)) );
            }
            return array;
        }

        return String.valueOf(object);
    }


}

