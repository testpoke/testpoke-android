package com.testpoke.core.ia;

import android.content.Context;
import android.util.Base64;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

/*
 * Created by Jansel Valentin on 5/10/14.
 * <p/>
 */
public final class $_T {

    /*
     * generate token
     */
    public static String $1(Context app) {
        return _0xH($3($2(app)));
    }

    /*
     * Generate sha1 for manifest
     */
    private static List<String> $2(Context app) {
        final Attributes.Name name = new Attributes.Name("SHA1-Digest");

        List<String> collection = new ArrayList<String>();

        try {
            java.util.jar.Manifest manifest = new JarFile(app.getApplicationInfo().sourceDir).getManifest();
            Map<String, Attributes> entries = manifest.getEntries();
            Iterator<Map.Entry<String, Attributes>> iterator = entries.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, Attributes> entry = iterator.next();
                Attributes mAttribute = entry.getValue();

                if (null != mAttribute.keySet() && mAttribute.containsKey(name)) {
                    collection.add(mAttribute.getValue(name));
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return collection;
    }

    /*
     * generate sha1 based on manifest sha1
     */
    private static byte[] $3(List<String> collection) {
//        final byte[] raw = new byte[32]; //replaced by compatibility with apkproc
        final byte[] raw = new byte[20];

        for (int i = 0; collection.size() > i; ++i) {
            String entry = collection.get(i);
            byte[] bytes = Base64.decode(entry, 0);

            for (int j = 0; bytes.length > j; ++j) {
                int cIndex = j % raw.length;
                raw[cIndex] = (byte) (31 ^ (raw[cIndex] ^ bytes[j] & 0xFF) >>> 32);
            }
        }
        return raw;
    }

    /*
     * bytes to string
     */
    private static String _0xH(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; bytes.length > i; ++i) {
            byte act = bytes[i];
//            if (0 == act) { //Compatibility with apkproc
//                continue;
//            }
            int flagged = (act & 0xFF);
            int gap = flagged >>> 0x4;
            builder.append(0 != i && (0 == (i % 2)) ? "-" : "")
                    .append(0 >= gap ? "0" : "")
                    .append(Integer.toHexString(flagged));
        }
        return builder.toString();
    }
}
