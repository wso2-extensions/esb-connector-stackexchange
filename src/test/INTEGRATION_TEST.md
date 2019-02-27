## Product: Integration tests for WSO2 ESB StackExchange Connector

### Pre-requisites:
 * Maven 3.x
 * Java 1.8

### Steps to follow in setting integration test.

1. Place the EI 6.4.0 zip file in <code><stackexchange_connector_home>/repository</code>.
2. Prerequisites for StackExchange Connector Integration Testing.
    1. First of all, you should have an App registered in <code>https://stackapps.com</code> to test <code>auth required</code> and other (see notes below) API endpoints. Follow instructions in <code>https://api.stackexchange.com/docs</code> to register an App.
    2. After creating the App follow API [authentication](https://api.stackexchange.com/docs/authentication) docs to get an <code>OAuth2.0</code> access token. You can also use [this](https://github.com/sujanan/se-token-generator) automation script to grab the token.
    3. According to StackExchange API docs all **Apps must have a registered Stack Apps post to write** to the site. You can create a placeholder post by following [these](https://stackapps.com/questions/4573/my-app-has-to-be-published-first-but-its-still-under-development) instructions.
    4. After creating the placeholder post, make sure to update the post url in your App in <code>https://stackapps.com/apps/oauth</code>.
    5. After completing above steps update <code>esb-connector-stackexchange.properties</code> file at location <code><stackexchange_connector_home>/repository</code> by following bellow instructions.
        #### Properties related to App
        1. **site**: which site you want to use for the tests. e.g. stackoverflow, askubuntu etc. Remove this line if you want to use the defaults.
        2. **accessToken**: Access Token you got from following step 2.2.
        3. **key**: You can find your App's <code>key</code> under your App configurations.
        #### Properties related to PlaceHolder Post
        In case you don't have any of the following data disable the line (comment out) for which you don't have so that those tests get skipped.
        
        1. **placeHolderQId**: Fill this with a question id that you own in the StackExchange network. This question should belong to the same account that you have been using so far while following above steps. You can simply put your App placeholder post's question id if you don't have any.
        2. **placeHolderAId**: Fill this with an answer id that you own in the StackExchange network. This question should belong to the same account that you have been using so far while following above steps. 
        3. **placeHolderSite**: Site which above ids belong to.
    
### Notes
#### API Key
In order to prevent abuse, the API implements a number of throttles. For that reason in most cases an API key 
should be passed when making requests against the Stack Exchange API to receive a [higher request quota](https://api.stackexchange.com/docs/throttle).
Even though it could be optional to pass the API key when sending requests to public API endpoints, 
while doing tests we assume key is mandatory for all endpoints. This is done to prevent confusing test failures due to API throttles.

#### Privileges 
StackExchange users will earn certain privileges when they reach required reputation. These reputation scores may differ from site to site.
If you have provided an account which lacks these privileges that we expect in our connector methods, we are skipping those tests. So make sure
to use an account with enough privileges to get all the tests without being skipped. 
