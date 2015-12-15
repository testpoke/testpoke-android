package com.testpoke.core.net.encoding.pack;

import com.testpoke.core.net.encoding.Encoder;

import java.io.IOException;

/*
 * Created by Jansel Valentin on 5/4/14.
 */
public final class PackEncoder implements Encoder<byte[], Object> {

    private static final PackEncoder DEFAULT = new PackEncoder();

    private PackEncoder() {
    }

    public static PackEncoder getDefault() {
        return DEFAULT;
    }

    @Override
    @Deprecated
    public byte[] encode(Object o) throws IOException {
//        MessagePack msgpack = new MessagePack();
//        BufferPacker packer = msgpack.createBufferPacker();
//        pack(packer, null, o);
//        byte[] data = packer.toByteArray();
//        packer.close();
//        return data;
        throw new UnsupportedOperationException("Method not supportted");
    }

//    private void pack(BufferPacker packer, String key, Object object) throws IOException {
//        if (null != key)
//            packer.write(key);
//
//        if (object instanceof String) {
//            packer.write(object.toString());
//        } else if (object instanceof Integer)
//            packer.write(Integer.valueOf(object.toString()));
//        else if (object instanceof Float)
//            packer.write(Float.valueOf(object.toString()));
//        else if (object instanceof Double)
//            packer.write(Double.valueOf(object.toString()));
//        else if (object instanceof Boolean)
//            packer.write(Boolean.valueOf(object.toString()));
//        else if (object instanceof Short)
//            packer.write(Short.valueOf(object.toString()));
//        else if (object instanceof Map) {
//            Map kvp = (Map) object;
//            Set<String> keys = kvp.keySet();
//
//            packer.writeMapBegin(keys.size());
//
//            for (String mkey : keys) {
//                pack(packer, mkey, kvp.get(mkey));
//            }
//            packer.writeMapEnd();
//        } else if (object.getClass().isArray()) {
//            Object[] array = (Object[]) object;
//            if (0 == array.length)
//                return;
//            packer.writeArrayBegin(array.length);
//            for (Object o : array)
//                pack(packer, null, o);
//
//            packer.writeArrayEnd();
//        }
//    }
}
