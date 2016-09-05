package com.symphony.demo.overrides;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OverridenHttpClient {

    private final KeyManagerFactory keyManagerFactory;
    private final TrustManagerFactory trustManagerFactory;
    private Proxy proxy;

    @Autowired
    public OverridenHttpClient(KeyManagerFactory keyManagerFactory, TrustManagerFactory trustManagerFactory, URL webProxyUrl) {
        this.keyManagerFactory = keyManagerFactory;
        this.trustManagerFactory = trustManagerFactory;

        if (null != webProxyUrl) {
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(webProxyUrl.getHost(), webProxyUrl.getPort()));
        } else {
            proxy = Proxy.NO_PROXY;
        }
    }

    public Client getClient() throws Exception {
        HttpUrlConnectorProvider connectorProvider = new HttpUrlConnectorProvider();

        HttpUrlConnectorProvider.ConnectionFactory connectionFactory = new HttpUrlConnectorProvider.ConnectionFactory() {

            @Override
            public HttpURLConnection getConnection(URL url) throws IOException {
                try {
                    final HttpsURLConnection httpsUrlConnection = (HttpsURLConnection) new URL(url.toExternalForm()).openConnection(proxy);
                    return httpsUrlConnection;
                } catch (final IOException e) {
                    throw new RuntimeException("Can't open connection to " + url, e);
                }
            }

        };
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        connectorProvider.connectionFactory(connectionFactory);

        final ClientConfig clientConfig = new ClientConfig();
        clientConfig.connectorProvider(connectorProvider);

        Client client = ClientBuilder.newBuilder().sslContext(sslContext).withConfig(clientConfig).build();
        return client;
    }

}
