# Configuring Viber Operations
[[Prerequisites]](#Prerequisites) [[Initializing the connector]](#initializing-the-connector)

## Prerequisites

> NOTE: StackExchange Connector implements both public methods and auth required methods. For public method you don't necessarily need to have an StackExchange account. But for the
 the other methods it's a must to have an StackExchange account and an App registered in StackApps.
 <br></br>
 
1. After creating an StackExchange account, you should have an App registered in <code>https://stackapps.com</code> to access <code>auth required</code> and other (see notes below) API endpoints. Follow instructions in <code>https://api.stackexchange.com/docs</code> to register an App.
2. After creating the App follow API [authentication](https://api.stackexchange.com/docs/authentication) docs to get an <code>OAuth2.0</code> access token.
3. According to StackExchange API docs all **Apps must have a registered Stack Apps post to write** to the site. You can create a placeholder post by following [these](https://stackapps.com/questions/4573/my-app-has-to-be-published-first-but-its-still-under-development) instructions.
4. After creating the placeholder post, make sure to update the post url in your App in <code>https://stackapps.com/apps/oauth</code>.
 
## Initializing the connector
Add the following <stackexchange.init> method in your configuration:

#### init
```xml
<stackexchange.init>
    <site>{$ctx:site}</site>
    <key>{$ctx:key}</key>
    <accessToken>{$ctx:accessToken}</accessToken>
</stackexchange.init>
```

##### Properties

* site:  Site you wish to access in StackExchange network. e.g. stackoverflow, askubuntu
* key: API key related to your App.
* accessToken: OAuth2.0 access token.
