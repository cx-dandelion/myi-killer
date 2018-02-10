//
// Decompiled by Jadx - 892ms
//
package com.netspace.library.utilities;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

public class SSLConnection$_FakeX509TrustManager implements X509TrustManager {
    private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[0];

    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        SSLConnection.access$0().checkClientTrusted(arg0, arg1);
    }

    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        SSLConnection.access$0().checkServerTrusted(arg0, arg1);
        String szSubjectDN = arg0[0].getSubjectDN().getName();
        if (!szSubjectDN.contains("O=宁波睿易教育科技股份有限公司") || !szSubjectDN.contains("CN=*.lexuewang.cn")) {
            throw new CertificateException("服务器证书无效");
        }
    }

    public X509Certificate[] getAcceptedIssuers() {
        return SSLConnection.access$0().getAcceptedIssuers();
    }
}
