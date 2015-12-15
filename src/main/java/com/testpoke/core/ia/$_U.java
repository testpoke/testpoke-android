package com.testpoke.core.ia;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import com.testpoke.TestPoke;

import java.util.UUID;

/*
 * Created by Jansel Valentin on 5/10/14.
 * <p/>
 * SupporterInfo
 */
public final class $_U {

    /*
     * getPackageName
     */
    public static String $1(Context context) {
        return context.getPackageName();
    }

    /*
     * getProcessID
     */
    public static String $2() {
        return String.valueOf(android.os.Process.myPid());
    }

    /*
     * generateUUID
     */
    public static String $3() {
        return UUID.randomUUID().toString();
    }

    /*
     * getImei
     */
    public static String $4(Context context) {
        if (!hasPermission(context, Manifest.permission.READ_PHONE_STATE))
            return $4();
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }


    public static String $4(){
        String id = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);
        String serial;
        try{
            serial = Build.class.getField("SERIAL").get(null).toString();
            return new UUID(id.hashCode(), serial.hashCode()).toString();
        }
        catch (Exception e){
            serial = "serial";
        }
        return new UUID(id.hashCode(), serial.hashCode()).toString();
    }


    public static String $5() {
        return "TestPoke/" + TestPoke.getVersion() + " (Android " + Build.VERSION.RELEASE + ") Device/";
    }


    public static boolean hasPermission(Context cxt, String permission) {
        final PackageManager pm = cxt.getPackageManager();
        if (pm == null) {
            return false;
        }
        try {
            return pm.checkPermission(permission, cxt.getPackageName()) == PackageManager.PERMISSION_GRANTED;
        } catch (RuntimeException e) {
            return false;
        }
    }
}
