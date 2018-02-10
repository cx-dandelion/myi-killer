//
// Decompiled by Jadx - 920ms
//
package com.netspace.library.utilities;

import android.util.Log;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SSLConnection {
    private static X509TrustManager mOriginalTrustmanager;
    private static SSLSocketFactory mSSLFactory;
    private static TrustManager[] trustManagers;

    public static SSLSocketFactory getSSLSocketFactory() {
        return mSSLFactory;
    }

    public static X509TrustManager getTrustManager() {
        return mOriginalTrustmanager;
    }

    public static synchronized void allowAllSSL() {
        synchronized (SSLConnection.class) {
            HttpsURLConnection.setDefaultHostnameVerifier(new 1());
            if (trustManagers == null) {
                trustManagers = new TrustManager[]{new _FakeX509TrustManager()};
                try {
                    TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    tmf.init(null);
                    mOriginalTrustmanager = (X509TrustManager) tmf.getTrustManagers()[0];
                } catch (Exception e) {
                }
            }
            try {
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, trustManagers, new SecureRandom());
                mSSLFactory = context.getSocketFactory();
                HttpsURLConnection.setDefaultSSLSocketFactory(mSSLFactory);
            } catch (NoSuchAlgorithmException e2) {
                Log.e("allowAllSSL", e2.toString());
            } catch (KeyManagementException e3) {
                Log.e("allowAllSSL", e3.toString());
            }
        }
    }
}
