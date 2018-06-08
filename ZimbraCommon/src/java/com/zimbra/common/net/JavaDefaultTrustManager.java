/*
 * 
 */

package com.zimbra.common.net;

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Default trust manager which uses Java trust store.
 *
 * @author pgajjar
 */
class JavaDefaultTrustManager implements X509TrustManager {
    private X509TrustManager keyStoreTrustManager;

    protected JavaDefaultTrustManager() throws GeneralSecurityException {
        TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        //passing null to init method check certificate in Java trust store(java-home/lib/security/cacerts)
        factory.init((KeyStore)null);
        TrustManager[] trustManagers = factory.getTrustManagers();
        for (TrustManager tm : trustManagers)
            if (tm instanceof X509TrustManager) {
                keyStoreTrustManager = (X509TrustManager)tm;
                return;
            }
        throw new KeyStoreException(TrustManagerFactory.getDefaultAlgorithm() + " trust manager not supported");
    }

    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        keyStoreTrustManager.checkClientTrusted(chain, authType);
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        keyStoreTrustManager.checkServerTrusted(chain, authType);
    }

    public X509Certificate[] getAcceptedIssuers() {
        return keyStoreTrustManager.getAcceptedIssuers();
    }
}