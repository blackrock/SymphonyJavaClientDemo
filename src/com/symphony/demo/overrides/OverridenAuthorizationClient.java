package com.symphony.demo.overrides;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientProperties;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.authenticator.api.AuthenticationApi;
import org.symphonyoss.symphony.authenticator.invoker.Configuration;
import org.symphonyoss.symphony.clients.AuthorizationClient;

/**
 * AuthorizationClient which retrieves only session token, not key manager token
 *
 */
public class OverridenAuthorizationClient extends AuthorizationClient {
    private final Logger LOG = Logger.getLogger(AuthorizationClient.class);

    private SymAuth symphonyAuthentication;
    private final String authUrl;
    private boolean loginStatus = false;

    public OverridenAuthorizationClient(String authUrl, String keyUrl) {
        super(authUrl, keyUrl);
        this.authUrl = authUrl;
    }

    @Override
    public SymAuth authenticate() throws Exception {
        symphonyAuthentication = new SymAuth();
        org.symphonyoss.symphony.authenticator.invoker.ApiClient authenticatorClient = Configuration.getDefaultApiClient();

        LOG.info("Authenticator client proxy: " + authenticatorClient.getHttpClient().getConfiguration().getProperty(ClientProperties.PROXY_URI));
        authenticatorClient.setBasePath(authUrl);

        // Get the authentication API
        AuthenticationApi authenticationApi = new AuthenticationApi(authenticatorClient);

        symphonyAuthentication.setSessionToken(authenticationApi.v1AuthenticatePost());
        LOG.info("SessionToken: {} : {}" + symphonyAuthentication.getSessionToken().getName() + symphonyAuthentication.getSessionToken().getToken());

        loginStatus = true;
        return symphonyAuthentication;
    }

    @Override
    public boolean isLoggedIn() {
        return loginStatus;
    }
}

