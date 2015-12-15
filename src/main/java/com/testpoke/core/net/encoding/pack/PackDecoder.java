package com.testpoke.core.net.encoding.pack;

import com.testpoke.core.net.encoding.Decoder;

import java.io.IOException;
import java.util.Map;

/*
 * Created by Jansel Valentin on 5/4/14.
 */
public final class PackDecoder implements Decoder<Map<String, Object>, byte[]> {

    private static final PackDecoder DEFAULT = new PackDecoder();

    private PackDecoder() {
    }

    public static PackDecoder getDefault() {
        return DEFAULT;
    }

    @Override
    @Deprecated
    public Map<String, Object> decode(byte[] values) throws IOException {
//        if (null == values)
//            return null;
//        MessagePack msgpack = new MessagePack();
//        BufferUnpacker unpacker = msgpack.createBufferUnpacker(values);
//
//        Map<String, Object> result = new HashMap<String, Object>();
//
//        unpack(unpacker.readValue(),null,result );
//        unpacker.close();
//        return result;
        throw new UnsupportedOperationException("Method not supportted");
    }
//
//    private void unpack(Value value, String key, Map<String, Object> result) throws IOException {
//
//        if (value.getType() == ValueType.INTEGER)
//            result.put(getDefaultNullableKey(key), unpack(value));
//        else if (value.getType() == ValueType.BOOLEAN)
//            result.put(getDefaultNullableKey(key), unpack(value));
//        else if (value.getType() == ValueType.FLOAT)
//            result.put(getDefaultNullableKey(key), unpack(value));
//        else if (value.getType() == ValueType.NIL)
//            result.put(getDefaultNullableKey(key), unpack(value));
//        else if (value.getType() == ValueType.ARRAY) {
//            result.put(getDefaultNullableKey(key), unpack(value));
//        }else if( value.getType() == ValueType.RAW){
//            result.put( getDefaultNullableKey(key),value.asRawValue().getString());
//        }else if (value.getType() == ValueType.MAP) {
//
//            Map<String, Object> mResult = result;
//            if (null != key)
//                result.put(key, mResult = new HashMap<String, Object>());
//
//            MapValue mapValue = (MapValue) value;
//
//            for(Map.Entry<Value,Value> entry : mapValue.entrySet() ){
//                String rKey  = entry.getKey().asRawValue().getString();
//                unpack(entry.getValue(),rKey,mResult );
//            }
//        }
//    }
//
//    private Object unpack(Value value) throws IOException {
//        if (value.getType() == ValueType.INTEGER)
//            return value.asIntegerValue();
//        else if (value.getType() == ValueType.BOOLEAN)
//            return value.asBooleanValue();
//        else if (value.getType() == ValueType.FLOAT)
//            return value.asFloatValue();
//        else if (value.getType() == ValueType.NIL)
//            return "";
//        else if( value.getType() == ValueType.RAW )
//            return value.asRawValue().getString();
//        else if (value.getType() == ValueType.ARRAY) {
//            ArrayValue array = (ArrayValue) value;
//            Object[] objects = new Object[array.size()];
//            for (int i = 0; array.size() > i; ++i) {
//                objects[i] = unpack(array.get(i));
//            }
//            return objects;
//        } else if (value.getType() == ValueType.MAP) {
//            HashMap<String, Object> result = new HashMap<String, Object>();
//            unpack(value, null, result);
//            return result;
//        }
//        return null;
//    }
//
//    private String getDefaultNullableKey(String key) {
//        return null == key ? "" : key;
//    }
}
