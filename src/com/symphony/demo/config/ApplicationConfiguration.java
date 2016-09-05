package com.symphony.demo.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {
    /**
     * Create a KeyManagerFactory for use with SSL, initialized with a certificate
     *
     * @return KeyManagerFactory to be used to authenticate with Symphony
     * @throws Exception Problems with the certificate, for example the wrong type or password, will throw a range of exceptions
     */
    @Bean
    public KeyManagerFactory keyManagerFactory() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        KeyStore keyStore = KeyStore.getInstance(System.getProperty("SYMPHONY_GATEWAY_CERTIFICATE_TYPE"));
        keyStore.load(classLoader.getResourceAsStream(System.getProperty("SYMPHONY_GATEWAY_CERTIFICATE")), certificatePassword());
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, certificatePassword());
        return keyManagerFactory;
    }

    /**
     * Create a TrustManagerFactory for use with SSL, initialized with a trust store
     *
     * @return TrustManagerFactory to verify the certificate from Symphony
     * @throws Exception Problems with the trust store, for example the wrong type or file path, will throw a range of exceptions
     */
    @Bean
    public TrustManagerFactory trustManagerFactory() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        KeyStore trustStore = KeyStore.getInstance(System.getProperty("SYMPHONY_GATEWAY_TRUST_STORE_TYPE"));
        trustStore.load(classLoader.getResourceAsStream(System.getProperty("SYMPHONY_GATEWAY_TRUST_STORE")), null);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        return trustManagerFactory;
    }

    /**
     * Reads proxy values from WEB_PROXY token. If the token is not set, it returns null.
     *
     * @return proxy URL or null
     * @throws MalformedURLException
     */
    @Bean
    public URL webProxyUrl() throws MalformedURLException {
        String webProxyUrlString = System.getProperty("WEB_PROXY");
        if (StringUtils.isBlank(webProxyUrlString)) {
            return null;
        } else {
            return new URL(webProxyUrlString);
        }
    }

    /**
     * Retrieves keystore password for the certificate from the file specified by SYMPHONY_GATEWAY_PASSWORD token.
     *
     * The file is ecnrypted with prodops keys.
     *
     * In development mode if SYMPHONY_GATEWAY_PASSWORD system property is set, the value is used as password.
     *
     * @return decrypted password
     */
    private char[] certificatePassword() {
        String passwordOrFilePath = System.getProperty("SYMPHONY_GATEWAY_PASSWORD");
        return (passwordOrFilePath.toCharArray());
    }
}
