# Working with Users in StackExchange

[ [Overview](#overview) ]  [ [Operation details](#operation-details) ]  [ [Sample configuration](#sample-configuration) ]

### Overview
The following operations allow you to work with users in StackExchange. Click an operation name to see details on how to use it. 
For a sample proxy service that illustrates how to work with users, see [Sample configuration](#sample-configuration).

| Operation | Description |
| ------------- |-------------|
| [getUsersByIds](#getting-the-set-of-users-identified-by-ids) | Return the information about about set of users. |

## Operation details
This section provides details on each of the operations.

### getting the set of users identified by ids
Return the information about set of users.

```xml
<stackexchange.getUsersByIds>
    <ids>{$ctx:ids}</ids>
    <filter>{$ctx:filter}</filter>
    <page>{$ctx:page}</page>
    <pagesize>{$ctx:pagesize}</pagesize>
    <min>{$ctx:min}</min>
    <max>{$ctx:max}</max>
    <fromdate>{$ctx:fromdate}</fromdate>
    <todate>{$ctx:todate}</todate>
    <order>{$ctx:order}</order>
    <sort>{$ctx:sort}</sort>
</stackexchange.getUsersByIds>
```

**Properties**
* ids: Semicolon delimited user ids or 'me' if you want to get information about yourself.
* filter: Filter you have chosen to filter fields in the response. e.g. default
* pagesize: Number of items should be stored in a single page.
* page: Which page to send among many pages in the response.
* fromdate: Starting date in fromdate to todate window
* todate: Ending date in fromdate to todate window
* min: Min value in min to max window
* max: Max value in min to max window
* sort: According to which field item list should be sorted  
* order: Sorting order ascending (asc) or descending (desc)

**Sample request**
```json
{
  "ids": "me",
  "site": "stackoverflow"
}
```

**Sample response**
```json
{
  "items": [
    {
      "badge_counts": {
        "bronze": 0,
        "silver": 0,
        "gold": 0
      },
      "account_id": 12508075,
      "is_employee": false,
      "last_modified_date": 1550148616,
      "last_access_date": 1552298923,
      "reputation_change_year": 15,
      "reputation_change_quarter": 15,
      "reputation_change_month": 0,
      "reputation_change_week": 0,
      "reputation_change_day": 0,
      "reputation": 16,
      "creation_date": 1513352330,
      "user_type": "registered",
      "user_id": 9104301,
      "location": "Sri Lanka",
      "link": "https://stackoverflow.com/users/9104301/dhanu",
      "profile_image": "https://lh6.googleusercontent.com/-fxyNTmd3fOc/AAAAAAAAAAI/AAAAAAAAADw/jTw3x-DCCJs/photo.jpg?sz=128",
      "display_name": "Dhanu"
    }
  ],
  "has_more": false,
  "quota_max": 10000,
  "quota_remaining": 9686
}
```

Following is a sample REST request that can be handled by the getUsersByIds operation.

**Related StackExchange API documentations**
* [https://api.stackexchange.com/docs/users-by-ids](https://api.stackexchange.com/docs/users-by-ids)
* [https://api.stackexchange.com/docs/me](https://api.stackexchange.com/docs/me)

## Sample configuration

Following example illustrates how to connect to StackExchange with the init operation and getUsersByIds operation.

1. Create a sample proxy as below :
```xml
<?xml version="1.0" encoding="utf-8"?>
<proxy name="stackexchange_getUsersByIds" startOnLoad="true" statistics="disable" trace="disable" transports="http,https" xmlns="http://ws.apache.org/ns/synapse">
  <target>
    <inSequence>
      <property expression="json-eval($.ids)" name="ids"/>
      <property expression="json-eval($.accessToken)" name="accessToken"/>
      <property expression="json-eval($.key)" name="key"/>
      <property expression="json-eval($.site)" name="site"/>
      <stackexchange.init>
        <site>{$ctx:site}</site>
        <key>{$ctx:key}</key>
        <accessToken>{$ctx:accessToken}</accessToken>
      </stackexchange.init>
      <stackexchange.getUsersByIds>
        <ids>{$ctx:ids}</ids>
      </stackexchange.getUsersByIds>
      <respond/>
    </inSequence>
    <outSequence/>
    <faultSequence/>
  </target>
</proxy>
```

2. Create a json file called `stackexchange_getUsersByIds.json` containing the following json:
```json
{
  "key": "<key>",
  "accessToken": "<access_token>",
  "site": "<site>",
  "ids": "me"
}
```

3. Replace site, key, access_token with your values.

4. Execute the following cURL command:
```
curl http://sujanan-ThinkPad-T530:8280/services/stackexchange_getUsersByIds -H 'Content-Type: application/json' -d @stackexchange_getUsersByIds.json
```

5. StackExchange will returns an json response as below :
```json
{
  "items": [
    {
      "badge_counts": {
        "bronze": 0,
        "silver": 0,
        "gold": 0
      },
      "account_id": 12508075,
      "is_employee": false,
      "last_access_date": 1552368972,
      "reputation_change_year": 0,
      "reputation_change_quarter": 0,
      "reputation_change_month": 0,
      "reputation_change_week": 0,
      "reputation_change_day": 0,
      "reputation": 1,
      "creation_date": 1536978020,
      "user_type": "registered",
      "user_id": 55362,
      "link": "https://stackapps.com/users/55362/dhanu",
      "profile_image": "https://lh6.googleusercontent.com/-fxyNTmd3fOc/AAAAAAAAAAI/AAAAAAAAADw/jTw3x-DCCJs/photo.jpg?sz=128",
      "display_name": "Dhanu"
    }
  ],
  "has_more": false,
  "quota_max": 10000,
  "quota_remaining": 9956
}
```
