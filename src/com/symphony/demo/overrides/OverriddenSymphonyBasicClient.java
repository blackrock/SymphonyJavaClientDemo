package com.symphony.demo.overrides;

import org.symphonyoss.client.impl.SymphonyBasicClient;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.clients.UsersClient;
import org.symphonyoss.symphony.clients.UsersFactory;

/**
 * Client to be used for demo - does not create services and only creates UsersClient
 *
 */
public class OverriddenSymphonyBasicClient extends SymphonyBasicClient {
    private UsersClient usersClient;

    @Override
    public boolean init(SymAuth symAuth, String email, String agentUrl, String serviceUrl) throws Exception {

        String NOT_LOGGED_IN_MESSAGE = "Currently not logged into Agent, please check certificates and tokens.";
        if (symAuth == null || symAuth.getSessionToken() == null || symAuth.getKeyToken() == null)
            throw new Exception("Symphony Authorization is not valid", new Throwable(NOT_LOGGED_IN_MESSAGE));

        if (agentUrl == null)
            throw new Exception("Failed to provide agent URL", new Throwable("Failed to provide agent URL"));

        if (serviceUrl == null)
            throw new Exception("Failed to provide service URL", new Throwable("Failed to provide service URL"));

        super.setSymAuth(symAuth);
        super.setAgentUrl(agentUrl);
        super.setServiceUrl(serviceUrl);

        usersClient = UsersFactory.getClient(this, UsersFactory.TYPE.DEFAULT);
        return true;
    }

    @Override
    public UsersClient getUsersClient() {
        return this.usersClient;
    }
}
