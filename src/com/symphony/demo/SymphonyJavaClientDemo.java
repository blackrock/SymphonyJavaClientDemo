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
import com.symphony.demo.overrides.OverriddenAuthorizationClient;
import com.symphony.demo.overrides.OverriddenHttpClient;

@Component
public class SymphonyJavaClientDemo {
    private static final Logger LOG = Logger.getLogger(SymphonyJavaClientDemo.class);
    
    // Get specific information from System properties 
    private static final String SESSION_AUTH_URL = System.getProperty("SESSION_AUTH_URL");
    private static final String KEY_AUTH_URL = System.getProperty("KEY_AUTH_URL");
    private static final String SYMPHONY_AGENT_URL = System.getProperty("SYMPHONY_AGENT_URL");
    private static final String SYMPHONY_POD_URL = System.getProperty("SYMPHONY_POD_URL");
    private static final String USER_EMAIL = System.getProperty("USER_EMAIL");

    private final OverriddenHttpClient overriddenHttpClient;

    @Autowired
    public SymphonyJavaClientDemo(OverriddenHttpClient overriddenHttpClient) {
        this.overriddenHttpClient = overriddenHttpClient;
    }
    
    private void startDemo() throws Exception {
        // STEP 1: Create clients
        SymphonyClient symphonyClient = new OverriddenSymphonyBasicClient();
        AuthorizationClient authClient = new OverriddenAuthorizationClient(SESSION_AUTH_URL, KEY_AUTH_URL);
        LOG.info("Clients created successfully");
        
        // STEP 2: Authenticate the client and get session-token
        SymAuth symphonyAuthenticator = authClient.authenticate();
        LOG.info("Clients authenticated successfully");
        LOG.info("Session Token: " + symphonyAuthenticator.getSessionToken().getToken() );
       
        // STEP 3: Initialise the client
        symphonyAuthenticator.setKeyToken(new Token());  // Add dummy key token
        symphonyClient.init(symphonyAuthenticator, USER_EMAIL, SYMPHONY_AGENT_URL, SYMPHONY_POD_URL);
        LOG.info("Clients initialised successfully");

        // STEP 4: Get user-id from user-email
        LOG.info("Retrieving user-id for: " + USER_EMAIL);
        Long userId = symphonyClient.getUsersClient().getUserFromEmail(USER_EMAIL).getId();
        LOG.info("User Id for " + USER_EMAIL + " is " + userId);
    }

    private void enableProxies() throws Exception {
        Client client = overriddenHttpClient.getHttpClient();

        // Authenticator API client 
        org.symphonyoss.symphony.authenticator.invoker.ApiClient defaultAuthClient = new org.symphonyoss.symphony.authenticator.invoker.ApiClient();
        defaultAuthClient.setHttpClient(client);
        org.symphonyoss.symphony.authenticator.invoker.Configuration.setDefaultApiClient(defaultAuthClient);

        // POD API client
        org.symphonyoss.symphony.pod.invoker.ApiClient defaultPodClient = new org.symphonyoss.symphony.pod.invoker.ApiClient();
        defaultPodClient.setHttpClient(client);
        org.symphonyoss.symphony.pod.invoker.Configuration.setDefaultApiClient(defaultPodClient);

        // Agent API client
        org.symphonyoss.symphony.agent.invoker.ApiClient defaultAgentClient = new org.symphonyoss.symphony.agent.invoker.ApiClient();
        defaultAgentClient.setHttpClient(client);
        org.symphonyoss.symphony.agent.invoker.Configuration.setDefaultApiClient(defaultAgentClient);
    }
    
    public static void main(String[] args) {
        try (ClassPathXmlApplicationContext beanFactory = new ClassPathXmlApplicationContext(new String[] { "spring/applicationContext.xml" })) {
            beanFactory.registerShutdownHook();
            beanFactory.getBean(SymphonyJavaClientDemo.class).startDemo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
