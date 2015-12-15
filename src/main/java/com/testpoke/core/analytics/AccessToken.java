package com.testpoke.core.analytics;

import android.Manifest;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.testpoke.TestPoke;
import com.testpoke.core.ia.$_T;
import com.testpoke.core.ia.$_U;
import com.testpoke.core.ia.Constants;
import com.testpoke.core.net.HttpRequest;
import com.testpoke.core.net.StandardEndpoint;
import com.testpoke.core.net.encoding.json.JsonDecoder;
import com.testpoke.core.net.encoding.json.JsonEncoder;
import com.testpoke.core.schedule.Recurrent;
import com.testpoke.core.schedule.Task;
import com.testpoke.core.util.Dump;
import com.testpoke.core.util.Tasks;
import com.testpoke.core.util.log.TP;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Created by Jansel Valentin on 5/10/14.
 */
/*package*/ final class AccessToken extends Task implements Recurrent {

    /*package*/ static final int MODE_TRUST = 1;
    /*package*/ static final int MODE_REVOKE = 2;

    /*package*/ static final int ACCESS_REJECTED = 3;
    /*package*/ static final int ACCESS_TRUSTED = 4;
    /*package*/ static final int ACCESS_REVOKED = 5;
    /*package*/ static final int ACCESS_RETRY_TIMEOUT = 6;


    private static final int MAX_RETRY_INTENT = 3;

    private long recurrentInterval = 3000;
    private int retryIntent;
    private List<OnAccessTokenListener> listeners;
    private Deliver deliver;
    private Context context;
    private int mode;

    private boolean enableRevokeSessions = false;


    AccessToken(Context context, int mode) {
        if (MODE_REVOKE == mode)
            deliver = new RevokeDeliver();
        else if (MODE_TRUST == mode)
            deliver = new TrustDeliver();

        this.mode = mode;
        this.context = context;
        listeners = new ArrayList<OnAccessTokenListener>();
    }

    @Override
    public int getId() {
        return deliver.id();
    }


    public long getRecurrentInterval() {
        if (MODE_REVOKE == mode)
            return 0;
        long _recurrentInterval = recurrentInterval;
        if (MAX_RETRY_INTENT > retryIntent && 0 != recurrentInterval) {
            retryIntent++;
            recurrentInterval *= 2;
        }
        return _recurrentInterval;
    }

    @Override
    protected void performTask() {
        deliver.deliver(this);
    }



    void registerOnAccessTokenListener(OnAccessTokenListener listener) {
        if (null == listener)
            return;

        if (listeners.contains(listener))
            return;
        listeners.add(listener);

    }

    void unregisterOnAccessTokenListener(OnAccessTokenListener listener) {
        if (null == listener)
            return;
        listeners.remove(listener);

    }

    void notifyListeners(int mode, int access, IA ia) {
        if (null == listeners)
            return;

        for (int i = listeners.size() - 1; i >= 0; --i) {
            OnAccessTokenListener listener = listeners.get(i);
            listener.onAccessToken(this, mode, access, ia);
        }
    }


    /*package*/ abstract interface OnAccessTokenListener {
        void onAccessToken(AccessToken at, int mode, int access, IA ia);
    }

    private abstract interface Deliver {
        void deliver(AccessToken at);

        int id();
    }


    private class TrustDeliver implements Deliver {
        byte[] raw;
        private String a;
        private String b = "";
        private boolean wasBundleSent;

        private void initBytes(AccessToken at) {
            IA.k()._ba8868af2($_T.$1(at.context));
            TP.i("Trusting bundle " + IA.k()._ba8868af2());

            Map<String, Object> map = new HashMap<String, Object>();
            map.put(Constants._ca0b77378, "");

            Map<String, Object> map2 = new HashMap<String, Object>();
            map2.put(Constants._tec489d1d, Constants._ca0b77378);

            Map<String, Object> map3 = new HashMap<String, Object>();
            map3.put(Constants._pc67af788, TestPoke.getSettings().getOptions().apiToken());
            map3.put(Constants._p80f47968, $_U.$1(at.context));
            map3.put(Constants._ba8868af2, IA.k()._ba8868af2());

            if (!(wasBundleSent = Installation.wasBundleSent(at.context)))
                map3.put(Constants._i9e9bf9b3, Installation.getBundle(at.context));

            map2.put(Constants._ve0a39b72, map3);
            map.put(Constants._eb56bb7f8, map2);
            try {
                raw = JsonEncoder.getDefault().encode(map);
            } catch (IOException ioe) {
                Dump.printStackTraceCause(ioe);
            }
            map.clear();
        }

        public int id() {
            return Tasks.ACCESS_TOKEN_TRUST;
        }

        public void deliver(AccessToken at) {
            if (null == raw) {
                initBytes(at);
                final String agent = $_U.$4(context);
                a = $_U.$5() + (TextUtils.isEmpty(agent) ? $_U.$4() : agent);

                String _b = "";
                if ($_U.hasPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    _b = null == tm.getDeviceId() ? "" : tm.getDeviceId();
                }

                b = "["+ $_U.$4()+"]:["+_b+"]";
                TP.i("Preparing to trust device " + a + " with entity " + b);
            }
            try {
                HttpResponse response = HttpRequest.executeHttpPost(StandardEndpoint.TRUST, raw, a,b);

                if (response.getStatusLine().getStatusCode() == 200) {
                    recurrentInterval = 0;

                    byte[] flat = EntityUtils.toByteArray(response.getEntity());
                    Map<String, Object> result = JsonDecoder.getDefault().decode(flat);

                    String k = null;
                    if (result.containsKey(Constants._s4a16cdfb)) { /*Request for status*/
                        Map<String, Object> result2 = (Map<String, Object>) result.get(Constants._s4a16cdfb);

                        if (result2.containsKey(Constants._tec489d1d) && Constants._s49f2b486.equals(result2.get(Constants._tec489d1d))) {

                            Map<String, Object> result3 = (Map<String, Object>) (result.get(Constants._eb56bb7f8)); /*entity*/
                            result3 = (Map<String, Object>) (result3.get(Constants._ve0a39b72)); /*entity.value*/

                            String _ade677c68 = result3.get(Constants._ade677c68).toString(); /*application_id*/
                            String _s7768b7aa = result3.get(Constants._s7768b7aa).toString(); /*session_id*/
                            String _ea8268b38 = result3.get(Constants._ea8268b38).toString(); /*environment*/

                            IA.k()._ade677c68(_ade677c68);
                            IA.k()._s7768b7aa(_s7768b7aa);
                            IA.k()._ea8268b38(_ea8268b38);

                            at.notifyListeners(MODE_TRUST, ACCESS_TRUSTED, IA.k());

                            if (!wasBundleSent)
                                Installation.bundleSent(at.context);

                        } else {
                            k = "due on " + result2.get(Constants._mc048b5b8); /*In status.type != SUCCESS then status.message is printed*/
                        }
                    } else {
                        k = "";
                    }
                    if (null != k) {
                        TP.e("Application registration could not be completed " + k);
                        at.notifyListeners(MODE_TRUST, ACCESS_REJECTED, null);
                    }
                    result.clear();
                } else {
                    retryConnection();
                }
            } catch (Exception ioe) {
                Dump.printStackTraceCause(ioe);
                retryConnection();
            }
        }


        private void retryConnection() {
            if (MAX_RETRY_INTENT == retryIntent) {
                recurrentInterval = 0;
                TP.e("Application registration could not be completed due on ACCESS_RETRY_TIMEOUT");
                notifyListeners(MODE_TRUST, ACCESS_RETRY_TIMEOUT, null);
            } else {
                TP.e("Application registration failed, retrying in " + recurrentInterval / 1E3 + " seconds");
                if (0 >= (recurrentInterval / 1E3)) {
                    notifyListeners(MODE_TRUST, ACCESS_REJECTED, null);
                }
            }
        }
    }

    private class RevokeDeliver implements Deliver {
        private byte[] raw;
        private String a;

        private void initRawBytes() {
            Map<String, Object> map = new HashMap<String, Object>();

            Map<String, String> map2 = new HashMap<String, String>();
            map.put(Constants._ca0b77378, map2);
            map2.put(Constants._s7768b7aa, IA.k()._s7768b7aa());

            try {
                raw = JsonEncoder.getDefault().encode(map);
            } catch (IOException ioe) {
                Dump.printStackTraceCause(ioe);
            }
        }

        public int id() {
            return Tasks.ACCESS_TOKEN_REVOKE;
        }

        public void deliver(AccessToken at) {

            if (null != IA.k()._s7768b7aa() && enableRevokeSessions) {
                if (null == raw) {
                    initRawBytes();
                    a = $_U.$5() + $_U.$4(context);
            }
            at.notifyListeners(MODE_REVOKE, ACCESS_REVOKED, null);
            getScheduler().remove(AccessToken.this);
            getScheduler().stop();
        }
    }
}
}

