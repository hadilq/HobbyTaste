package ir.asparsa.hobbytaste.core.retrofit;

/**
 * @author hadi
 * @since 3/12/2017 AD.
 */

import android.content.Context;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.BuildConfig;
import ir.asparsa.hobbytaste.R;

import javax.inject.Inject;
import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

/**
 * Allows you to trust certificates from additional KeyStores in addition to
 * the default KeyStore
 */
public class AdditionalKeyStoresSSLSocketFactory extends SSLSocketFactory {
    private final AdditionalKeyStoresTrustManager mTrustManager;
    private final SSLContext mSslContext = SSLContext.getInstance("TLS");

    @Inject
    Context mContext;

    public AdditionalKeyStoresSSLSocketFactory()
            throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException,
                   IOException, CertificateException {
        super();
        ApplicationLauncher.mainComponent().inject(this);

        KeyStore ksTrust = KeyStore.getInstance("BKS");
        InputStream inputStream = mContext.getResources().openRawResource(R.raw.mystore);
        try {
            ksTrust.load(inputStream, "secret".toCharArray());
        } finally {
            inputStream.close();
        }

        if (BuildConfig.DEBUG) {
            Enumeration enumeration = ksTrust.aliases();
            while (enumeration.hasMoreElements()) {
                String alias = (String) enumeration.nextElement();
                L.i(getClass(), "alias name: " + alias);
                java.security.cert.Certificate certificate = ksTrust.getCertificate(alias);
                L.i(getClass(), certificate.toString());
            }
        }

        mTrustManager = new AdditionalKeyStoresTrustManager(ksTrust);
        mSslContext.init(null, new TrustManager[]{mTrustManager}, new SecureRandom());
    }

    public AdditionalKeyStoresTrustManager getTrustManager() {
        return mTrustManager;
    }

    @Override public String[] getDefaultCipherSuites() {
        return mSslContext.getSocketFactory().getDefaultCipherSuites();
    }

    @Override public String[] getSupportedCipherSuites() {
        return mSslContext.getSocketFactory().getSupportedCipherSuites();
    }

    @Override public Socket createSocket(
            Socket s,
            String host,
            int port,
            boolean autoClose
    ) throws IOException {
        return mSslContext.getSocketFactory().createSocket(s, host, port, autoClose);
    }

    @Override public Socket createSocket(
            String host,
            int port
    ) throws IOException, UnknownHostException {
        return mSslContext.getSocketFactory().createSocket(host, port);
    }

    @Override public Socket createSocket(
            String host,
            int port,
            InetAddress localHost,
            int localPort
    ) throws IOException, UnknownHostException {
        return mSslContext.getSocketFactory().createSocket(host, port, localHost, localPort);
    }

    @Override public Socket createSocket(
            InetAddress host,
            int port
    ) throws IOException {
        return mSslContext.getSocketFactory().createSocket(host, port);
    }

    @Override public Socket createSocket(
            InetAddress address,
            int port,
            InetAddress localAddress,
            int localPort
    ) throws IOException {
        return mSslContext.getSocketFactory().createSocket(address, port, localAddress, localPort);
    }


    /**
     * Based on http://download.oracle.com/javase/1.5.0/docs/guide/security/jsse/JSSERefGuide.html#X509TrustManager
     */
    public static class AdditionalKeyStoresTrustManager implements X509TrustManager {

        protected ArrayList<X509TrustManager> x509TrustManagers = new ArrayList<>();


        protected AdditionalKeyStoresTrustManager(KeyStore... additionalKeyStores) {
            final ArrayList<TrustManagerFactory> factories = new ArrayList<>();

            try {
                // The default TrustManager with default keystore
                final TrustManagerFactory original = TrustManagerFactory
                        .getInstance(TrustManagerFactory.getDefaultAlgorithm());
                original.init((KeyStore) null);
                factories.add(original);

                for (KeyStore keyStore : additionalKeyStores) {
                    final TrustManagerFactory additionalCerts = TrustManagerFactory
                            .getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    additionalCerts.init(keyStore);
                    factories.add(additionalCerts);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            /*
             * Iterate over the returned TrustManagers, and hold on
             * to any that are X509TrustManagers
             */
            for (TrustManagerFactory tmf : factories)
                for (TrustManager tm : tmf.getTrustManagers())
                    if (tm instanceof X509TrustManager)
                        x509TrustManagers.add((X509TrustManager) tm);


            if (x509TrustManagers.size() == 0)
                throw new RuntimeException("Couldn't find any X509TrustManagers");

        }

        /**
         * Delegate to the default trust manager.
         */
        @Override
        public void checkClientTrusted(
                X509Certificate[] chain,
                String authType
        ) throws CertificateException {
            final X509TrustManager defaultX509TrustManager = x509TrustManagers.get(0);
            defaultX509TrustManager.checkClientTrusted(chain, authType);
        }

        /**
         * Loop over the TrustManagers until we find one that accepts our server
         */
        @Override
        public void checkServerTrusted(
                X509Certificate[] chain,
                String authType
        ) throws CertificateException {
            Exception originalException = null;
            for (X509TrustManager tm : x509TrustManagers) {
                try {
                    tm.checkServerTrusted(chain, authType);
                    return;
                } catch (CertificateException e) {
                    if (originalException == null) {
                        originalException = e;
                    }
                }
            }
            L.i(getClass(), "checkServerTrusted is failed");
            throw new CertificateException(originalException);
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            final ArrayList<X509Certificate> list = new ArrayList<>();
            for (X509TrustManager tm : x509TrustManagers)
                list.addAll(Arrays.asList(tm.getAcceptedIssuers()));
            return list.toArray(new X509Certificate[list.size()]);
        }
    }

}