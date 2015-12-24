package com.opdar.seed.extra.apns;

import javax.net.ssl.*;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by 俊帆 on 2015/12/21.
 */
public class Constants {
    public static final String DEVELOPMENT_SERVER = "api.development.push.apple.com:443";
    public static final String PRODUCTION_SERVER = "api.push.apple.com:443";

    private static String algorithm = "sunx509";
    private static KeyManagerFactory keyManagerFactory;
    private static TrustManager[] trustManagers = new TrustManager[] { new X509TrustManagerTrustAll() };
    private static SSLContext sslContext;
    private static final String KEYSTORE_TYPE = "PKCS12";

    static class X509TrustManagerTrustAll implements X509TrustManager {
        public boolean checkClientTrusted(java.security.cert.X509Certificate[] chain){
            return true;
        }

        public boolean isServerTrusted(java.security.cert.X509Certificate[] chain){
            return true;
        }

        public boolean isClientTrusted(java.security.cert.X509Certificate[] chain){
            return true;
        }

        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {}

        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {}
    }
    public static void build(InputStream keyStoreStream, String keyStorePassword, String keyStoreType){
        try {
            keyManagerFactory = KeyManagerFactory.getInstance(algorithm);
//            keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

            final KeyStore ks = KeyStore.getInstance(keyStoreType);
            ks.load(keyStoreStream, keyStorePassword.toCharArray());

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(algorithm);
            trustManagerFactory.init(ks);
            trustManagers = trustManagerFactory.getTrustManagers();

            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagers, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
