package com.symphony.demo;

import javax.ws.rs.client.Client;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.model.SymAuth;
import org.symphonyoss.symphony.authenticator.model.Token;
import org.symphonyoss.symphony.clients.AuthorizationClient;

import com.symphony.demo.overrides.OverriddenSymphonyBasicClient;
import com.symphony.demo.overrides.OverridenAuthorizationClient;
import com.symphony.demo.overrides.OverridenHttpClient;

@Component
public class SymphonyJavaClientDemo {

    private static final Logger LOG = Logger.getLogger(SymphonyJavaClientDemo.class);

    private final String sessionAuthUrl;
    private final String keyAuthUrl;
    private final String symphonyAgentUrl;
    private final String symphonyPodUrl;
    private final OverridenHttpClient overridenHttpClient;

    @Autowired
    public SymphonyJavaClientDemo(OverridenHttpClient overridenHttpClient, String sessionAuthUrl, String keyAuthUrl, String symphonyAgentUrl, String symphonyPodUrl) {
        this.sessionAuthUrl = sessionAuthUrl;
        this.keyAuthUrl = keyAuthUrl;
        this.symphonyAgentUrl = symphonyAgentUrl;
        this.symphonyPodUrl = symphonyPodUrl;
        this.overridenHttpClient = overridenHttpClient;
    }

    private void start() throws Exception {
        overrideDefaultClients();
        
        SymphonyClient symphonyClient = new OverriddenSymphonyBasicClient();
        AuthorizationClient authClient = new OverridenAuthorizationClient(sessionAuthUrl, keyAuthUrl);
        LOG.info("Clients initiated successfully");
        
        SymAuth symAuth = authClient.authenticate();
        LOG.info("Clients authenticated successfully");
        LOG.info("Session Token: " + symAuth.getSessionToken().getToken() );
       
        symAuth.setKeyToken(new Token());  // Add dummy key token
        String userEmail = System.getProperty("USER_EMAIL");
        symphonyClient.init(symAuth, userEmail, symphonyAgentUrl, symphonyPodUrl);
        LOG.info("Retrieving user-id for: " + userEmail);
        Long userId = symphonyClient.getUsersClient().getUserFromEmail(userEmail).getId();
        LOG.info("User Id for " + userEmail + " is " + userId);
    }

    public static void main(String[] args) {
        try (ClassPathXmlApplicationContext beanFactory = new ClassPathXmlApplicationContext(new String[] { "spring/applicationContext.xml" })) {
            beanFactory.registerShutdownHook();
            beanFactory.getBean(SymphonyJavaClientDemo.class).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void overrideDefaultClients() throws Exception {
        Client client = overridenHttpClient.getClient();
        org.symphonyoss.symphony.authenticator.invoker.ApiClient defaultAuthClient = new org.symphonyoss.symphony.authenticator.invoker.ApiClient();
        org.symphonyoss.symphony.pod.invoker.ApiClient defaultPodClient = new org.symphonyoss.symphony.pod.invoker.ApiClient();
        org.symphonyoss.symphony.agent.invoker.ApiClient defaultAgentClient = new org.symphonyoss.symphony.agent.invoker.ApiClient();
        defaultAuthClient.setHttpClient(client);
        defaultPodClient.setHttpClient(client);
        defaultAgentClient.setHttpClient(client);
        org.symphonyoss.symphony.authenticator.invoker.Configuration.setDefaultApiClient(defaultAuthClient);
        org.symphonyoss.symphony.pod.invoker.Configuration.setDefaultApiClient(defaultPodClient);
        org.symphonyoss.symphony.agent.invoker.Configuration.setDefaultApiClient(defaultAgentClient);
    }
}
