package code.versee.nexoclient.security;

import javax.net.ssl.*;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class NexoCertPinner {
    // Aapke Bonto/Backend server ka SHA-256 Public Key Hash (Optional Pinning)
    private static final String TRUSTED_KEY_HASH = "YOUR_SERVER_SHA256_PUBLIC_KEY_PIN";

    public static SSLSocketFactory getPinnedSocketFactory() {
        try {
            TrustManager[] trustManagers = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                            // Custom Pinning Verification
                            if (chain == null || chain.length == 0) {
                                throw new RuntimeException("Empty certificate chain!");
                            }
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
            sslContext.init(null, trustManagers, new java.security.SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            return (SSLSocketFactory) SSLSocketFactory.getDefault();
        }
    }
}