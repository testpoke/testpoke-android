package com.testpoke.core.analytics;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.telephony.TelephonyManager;
import com.testpoke.TestPoke;
import com.testpoke.core.ia.$_U;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
 * Created by Jansel Valentin on 6/24/2014.
 */
final class Installation {

    private static final String SDK_VERSION_PREF = "com.testpoke.SDK_VERSION_PREF";
    private static final String SDK_VERSION_KEY = "com.testpoke.SDK_VERSION";


    private static final boolean isSdkVersionRecognized( Context context,String sdkVersion ){
        SharedPreferences prefs = context.getSharedPreferences( SDK_VERSION_PREF,Context.MODE_PRIVATE);
        return  prefs.getString( SDK_VERSION_KEY, "" ).equals( sdkVersion );
    }


    private static final void recognizeSdkVersion( Context context, String sdkVersion ){
        SharedPreferences prefs = context.getSharedPreferences( SDK_VERSION_PREF, Context.MODE_PRIVATE );
        prefs.edit().putString(SDK_VERSION_KEY, sdkVersion).commit();
    }

    public static boolean wasBundleSent(Context context){
        return isSdkVersionRecognized(context, TestPoke.getVersion());
    }



    public static void bundleSent(Context context){
        recognizeSdkVersion(context, TestPoke.getVersion());
    }

    public static Map<String,Object> getBundle(Context context ) {
        Map<String,Object> bundle = new HashMap<String, Object>();

        if( $_U.hasPermission(context, Manifest.permission.READ_PHONE_STATE) ){
            TelephonyManager tm = (TelephonyManager) context.getSystemService( Context.TELEPHONY_SERVICE );
            bundle.put( "imei",null );
            bundle.put( "mcc",tm.getNetworkCountryIso());
            bundle.put( "non",tm.getNetworkOperatorName());
        }

        bundle.put( "id", $_U.$4(context) ); //imei if app has READ_PHONE_STATE permission, pseudo-id otherwise
        bundle.put( "serial", $_U.$4());
        bundle.put( "brand", Build.BRAND );
        bundle.put( "product", Build.PRODUCT );
        bundle.put( "device", Build.DEVICE );
        bundle.put( "cpu_abi", Build.CPU_ABI );
        bundle.put( "display" , Build.DISPLAY );
        bundle.put( "hardware", Build.HARDWARE );
        bundle.put( "device_id", Build.ID );
        bundle.put( "manuf", Build.MANUFACTURER );
        bundle.put( "model", Build.MODEL );
        bundle.put( "sdk",Build.VERSION.SDK );
        bundle.put( "sdk_int",Build.VERSION.SDK_INT );
        bundle.put( "installed_at",new Date().toString() );

        return bundle;
    }
}
