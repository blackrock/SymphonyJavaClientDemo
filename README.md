# Symphony Java Client Demo

The objective of this repository to have a simple demo to connect Symphony (https://symphony.com/) using their REST APIs (https://developers.symphony.com/). 

## Pre-requisite for this is as follows:
1. A Symphony POD to connect to
2. Certificates & Trust Store to connect to this Symphony POD

## Known Tokens
* SYMPHONY_GATEWAY_CERTIFICATE - path to the keystore containing certificate
* SYMPHONY_GATEWAY_PASSWORD - path to the file containing encrypted password for the keystore
* SYMPHONY_GATEWAY_TRUST_STORE - path to the trust store for verifying certificate from Symphony during SSL
* SYMPHONY_GATEWAY_CERTIFICATE_TYPE - type of certificate being used (e.g. pkcs12)
* SYMPHONY_GATEWAY_TRUST_STORE_TYPE - type of the trust store being used (e.g. jks)
* SYMPHONY_POD_URL - url of Symphony pod
* SESSION_AUTH_URL - url for getting session token from Symphony
* KEY_AUTH_URL - url for getting key manager token from key manager
* SYMPHONY_AGENT_URL - url of agent
* WEB_PROXY - proxy to use while connecting to Symphony from a corporate setup
* USER_EMAIL - email required while initiating SymphonyOSS 
