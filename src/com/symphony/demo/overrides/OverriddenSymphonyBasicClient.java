package com.symphony.demo.overrides;

import org.apache.log4j.Logger;
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
    private static final Logger LOG = Logger.getLogger(OverriddenSymphonyBasicClient.class);

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

        try {

            usersClient = UsersFactory.getClient(this, UsersFactory.TYPE.DEFAULT);
        } catch (Exception e) {
            LOG.error("Could not initialize one of the Symphony API services." + " This is most likely due to not having the right agent or pod URLs."
                    + " This can also be an issue with the client certificate or server.trustore." + " Here is what you have configured: {}");

        }
        return true;
    }

    @Override
    public UsersClient getUsersClient() {
        return this.usersClient;
    }
}
