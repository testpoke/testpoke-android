package com.testpoke.core.net;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

/*
 * Created by Jansel Valentin on 5/4/14.
 */
public final class HttpRequest {
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int SOCKET_TIMEOUT = 7000;

    private static final ClientConnectionManager mClientConnectionManager = createConnectionManager();

    public static HttpResponse executeHttpPost(StandardEndpoint endpoint, byte[] data, String userAgent, String userEntity) throws IOException {

        HttpEntity entity = new ByteArrayEntity(data);
        HttpPost post = new HttpPost(endpoint.endpoint());
        post.setEntity(entity);

        Map<String, String> headers = getHeaders(userAgent, userEntity, MediaTypes.APPLICATION_JSON);

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            post.addHeader(entry.getKey(), entry.getValue());
        }

        BasicHttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT);

        HttpClient client = null != mClientConnectionManager
            ? new DefaultHttpClient(mClientConnectionManager,params)
            : new DefaultHttpClient(params);


        HttpResponse response = client.execute(post);
        return response;
    }

    private static Map<String, String> getHeaders(String userAgent, String userEntity, String transport) {
        return getHeaders(userAgent, userEntity, transport, transport);
    }

    private static Map<String, String> getHeaders(String userAgent, String userEntity, String transport, String accept) {
        Map<String, String> headers = new HashMap<String, String>();

        headers.put(HTTP.CONTENT_TYPE, transport);
        headers.put("Accept", accept);
        headers.put(HTTP.USER_AGENT, userAgent);

        if (null != userEntity)
            headers.put("User-Entity", userEntity);

        return headers;
    }

    private static ClientConnectionManager createConnectionManager( ){
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            BypassSSLSocketFactory ssf = new BypassSSLSocketFactory( trustStore );
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", ssf, 443));

            return new ThreadSafeClientConnManager( new BasicHttpParams(),registry);
        }catch( Exception ex ){
            ex.printStackTrace();
        }
        return null;
    }

}
