# Working with Answers in StackExchange

[ [Overview](#overview) ]  [ [Operation details](#operation-details) ]  [ [Sample configuration](#sample-configuration) ]

### Overview
The following operations allow you to work with answers in StackExchange. Click an operation name to see details on how to use it. 
For a sample proxy service that illustrates how to work with answers, 
see [Sample configuration](#sample-configuration).

| Operation | Description |
| ------------- |-------------|
| [getAnswersByIds](#getting-the-set-of-answers-identified-by-ids) | Return the set of answers identified by ids. |
| [addAnswer](#adding-an-answer) | Create a new answer. |
| [deleteAnswerById](#deleting-an-answer) | Delete an existing answer. |
| [editAnswerById](#editing-an-answer) | Edit an existing answer. |
| [upvoteAnswerById](#upvoting-an-answer) | Upvote an existing answer. |
| [downvoteAnswerById](#downvoting-an-answer) | Downvote an existing answer. |
| [acceptAnswerById](#Accepting-an-answer) | Accept an existing answer. |

## Operation details
This section provides details on each of the operations.

### getting the set of answers identified by ids
Return the set of answers identified by ids.

**getAnswersByIds**
```xml
<stackexchange.getAnswersByIds>
    <ids>{$ctx:ids}</ids>
    <filter>{$ctx:filter}</filter>
    <sort>{$ctx:sort}</sort>
    <page>{$ctx:page}</page>
    <max>{$ctx:max}</max>
    <fromdata>{$ctx:fromdata}</fromdata>
    <order>{$ctx:order}</order>
    <pagesize>{$ctx:pagesize}</pagesize>
    <min>{$ctx:min}</min>
    <todate>{$ctx:todate}</todate>
</stackexchange.getAnswersByIds>
```

**Properties**
* ids: Semicolon delimited answer ids. e.g. 1;2;3
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
  "ids": 8239,
  "pagesize": 1,
  "page": 1
}
```

**Sample response**
```json
{
  "items": [
    {
      "owner": {
        "reputation": 10790,
        "user_id": 7653,
        "user_type": "moderator",
        "accept_rate": 75,
        "profile_image": "https://www.gravatar.com/avatar/08ad3e87a75ff0936395b59325d8b151?s=128&d=identicon&r=PG",
        "display_name": "Brock Adams",
        "link": "https://stackapps.com/users/7653/brock-adams"
      },
      "is_accepted": true,
      "score": 1,
      "last_activity_date": 1550356234,
      "last_edit_date": 1550356234,
      "creation_date": 1550355696,
      "answer_id": 8239,
      "question_id": 8238
    }
  ],
  "has_more": false,
  "quota_max": 300,
  "quota_remaining": 181
}
```

Following is a sample REST request that can be handled by the getAnswersByIds operation.

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/answers-by-ids](https://api.stackexchange.com/docs/answers-by-ids)

### Adding an answer
Create a new answer.

**addAnswer**
```xml
<stackexchange.addAnswer>
    <id>{$ctx:id}</id>
    <postBody>{$ctx:postBody}</postBody>
    <preview>{$ctx:preview}</preview>
    <filter>{$ctx:filter}</filter>
</stackexchange.addAnswer>
```

**Properties**
* id: Id of the question to add an answer. e.g. 8940
* postBody: Body of the answer.
* filter: Filter you have chosen to filter fields in the response. e.g. default
* preview: Preview parameter is used for development and testing purposes. When preview is set to true,
           request will only simulate whether it is a valid request or not and will not change
           anything on the site.

**Sample request**
```json
{
  "id": 1,
  "postBody": "Branch prediction: With a sorted array, the condition data[c] >= 128 is first false for a streak of values, then becomes true for all later values. That's easy to predict. With an unsorted array, you pay for the branching cost",
  "preview": true
}
```

**Sample response**
```json
{
  "items": [
    {
      "owner": {
        "reputation": 1,
        "user_id": 58519,
        "user_type": "registered",
        "profile_image": "https://lh5.googleusercontent.com/-kVyGXuaKbiQ/AAAAAAAAAAI/AAAAAAAAAAA/ACevoQM6oMuunuZdDgfqW9iGK8svF3jnmA/mo/photo.jpg?sz=128",
        "display_name": "Bhathiya Wijesinghe",
        "link": "https://stackapps.com/users/58519/bhathiya-wijesinghe"
      },
      "is_accepted": false,
      "score": 0,
      "last_activity_date": 1552284775,
      "creation_date": 1552284775,
      "answer_id": 0,
      "question_id": 1
    }
  ],
  "has_more": false,
  "quota_max": 10000,
  "quota_remaining": 9974
}
```

Following is a sample REST request that can be handled by the addAnswer operation.

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/create-answer](https://api.stackexchange.com/docs/create-answer)

### Deleting an answer
Delete an existing answer.

**deleteAnswerById**
```xml
<stackexchange.deleteAnswerById>
    <id>{$ctx:id}</id>
    <preview>{$ctx:preview}</preview>
    <filter>{$ctx:filter}</filter>
</stackexchange.deleteAnswerById>
```

**Properties**
* ids: Id of the answer. e.g. 8940
* filter: Filter you have chosen to filter fields in the response. e.g. default
* preview: Preview parameter is used for development and testing purposes. When preview is set to true,
           request will only simulate whether it is a valid request or not and will not change
           anything on the site.

**Sample request**
```json
{
  "site": "buddhism",
  "id": 31476,
  "preview": true
}
```

**Sample response**
```json
{
  "items": [],
  "has_more": false,
  "quota_max": 10000,
  "quota_remaining": 9783
}
```

Following is a sample REST request that can be handled by the deleteAnswer operation.

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/delete-answer](https://api.stackexchange.com/docs/delete-answer)

### Editing an answer
Edit an existing answer.

**editAnswerById**
```xml
<stackexchange.editAnswerById>
    <id>{$ctx:id}</id>
    <postBody>{$ctx:postBody}</postBody>
    <preview>{$ctx:preview}</preview>
    <filter>{$ctx:filter}</filter>
</stackexchange.editAnswerById>
```

**Properties**
* id: Id of the answer. e.g. 8940
* postBody: Body of the answer.
* filter: Filter you have chosen to filter fields in the response. e.g. default
* preview: Preview parameter is used for development and testing purposes. When preview is set to true,
           request will only simulate whether it is a valid request or not and will not change
           anything on the site.

**Sample request**
```json
{
  "id": 54664724,
  "postBody": "A simple compressed response from URLConnection instance should be decompressed if Accept-Encoding headers are set correctly. But it doesn't seems to be working. Have you ever experienced this type of problem?",
  "preview": true
}
```

**Sample response**
```json
{
  "items": [
    {
      "owner": {
        "reputation": 16,
        "user_id": 9104301,
        "user_type": "registered",
        "profile_image": "https://lh6.googleusercontent.com/-fxyNTmd3fOc/AAAAAAAAAAI/AAAAAAAAADw/jTw3x-DCCJs/photo.jpg?sz=128",
        "display_name": "Dhanu",
        "link": "https://stackoverflow.com/users/9104301/dhanu"
      },
      "is_accepted": false,
      "score": 0,
      "last_activity_date": 1552329565,
      "last_edit_date": 1552329565,
      "creation_date": 1550042874,
      "answer_id": 54664724,
      "question_id": 54348603
    }
  ],
  "has_more": false,
  "quota_max": 10000,
  "quota_remaining": 9691
}
```

Following is a sample REST request that can be handled by the editAnswerById operation.

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/edit-answer](https://api.stackexchange.com/docs/edit-answer)

### Upvoting an answer
Upvote an existing answer.

**upvoteAnswerById**
```xml
<stackexchange.upvoteAnswerById>
    <id>{$ctx:id}</id>
    <preview>{$ctx:preview}</preview>
    <filter>{$ctx:filter}</filter>
</stackexchange.upvoteAnswerById>
```

**Properties**
* ids: Id of the answer. e.g. 8940
* filter: Filter you have chosen to filter fields in the response. e.g. default
* preview: Preview parameter is used for development and testing purposes. When preview is set to true,
           request will only simulate whether it is a valid request or not and will not change
           anything on the site.

**Sample request**
```json
{
  "id": 55108393,
  "preview": true
}
```

**Sample response**
```json
{
  "items": [
    {
      "owner": {
        "reputation": 1294,
        "user_id": 425465,
        "user_type": "registered",
        "profile_image": "https://www.gravatar.com/avatar/177b8d33a63edd4ff1c150130f554f83?s=128&d=identicon&r=PG",
        "display_name": "harlam357",
        "link": "https://stackoverflow.com/users/425465/harlam357"
      },
      "is_accepted": false,
      "score": 1,
      "last_activity_date": 1552329779,
      "creation_date": 1552329779,
      "answer_id": 55108393,
      "question_id": 55105701
    }
  ],
  "has_more": false,
  "quota_max": 10000,
  "quota_remaining": 9688
}
```

Following is a sample REST request that can be handled by the upvoteAnswerById operation.

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/upvote-answer](https://api.stackexchange.com/docs/upvote-answer)

### Downvoting an answer
Downvote an existing answer.

**downvoteAnswerById**
```xml
<stackexchange.downvoteAnswerById>
    <id>{$ctx:id}</id>
    <preview>{$ctx:preview}</preview>
    <filter>{$ctx:filter}</filter>
</stackexchange.downvoteAnswerById>
```

**Properties**
* ids: Id of the answer. e.g. 8940
* filter: Filter you have chosen to filter fields in the response. e.g. default
* preview: Preview parameter is used for development and testing purposes. When preview is set to true,
           request will only simulate whether it is a valid request or not and will not change
           anything on the site.

**Sample request**
```json
{
  "id": 55108393,
  "preview": true
}
```

**Sample response**
```json
{
  "items": [
    {
      "owner": {
        "reputation": 1294,
        "user_id": 425465,
        "user_type": "registered",
        "profile_image": "https://www.gravatar.com/avatar/177b8d33a63edd4ff1c150130f554f83?s=128&d=identicon&r=PG",
        "display_name": "harlam357",
        "link": "https://stackoverflow.com/users/425465/harlam357"
      },
      "is_accepted": false,
      "score": 1,
      "last_activity_date": 1552329779,
      "creation_date": 1552329779,
      "answer_id": 55108393,
      "question_id": 55105701
    }
  ],
  "has_more": false,
  "quota_max": 10000,
  "quota_remaining": 9688
}
```

Following is a sample REST request that can be handled by the downvoteAnswerById operation.

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/downvote-answer](https://api.stackexchange.com/docs/downvote-answer)

### Accepting an answer
Accept an existing answer.

**acceptAnswerById**
```xml
<stackexchange.acceptAnswerById>
    <id>{$ctx:id}</id>
    <preview>{$ctx:preview}</preview>
    <filter>{$ctx:filter}</filter>
</stackexchange.acceptAnswerById>
```

**Properties**
* ids: Id of the answer. e.g. 8940
* filter: Filter you have chosen to filter fields in the response. e.g. default
* preview: Preview parameter is used for development and testing purposes. When preview is set to true,
           request will only simulate whether it is a valid request or not and will not change
           anything on the site.

**Sample request**
```json
{
  "id": 55108393,
  "preview": true
}
```

**Sample response**
```json
{
  "items": [
    {
      "owner": {
        "reputation": 1294,
        "user_id": 425465,
        "user_type": "registered",
        "profile_image": "https://www.gravatar.com/avatar/177b8d33a63edd4ff1c150130f554f83?s=128&d=identicon&r=PG",
        "display_name": "harlam357",
        "link": "https://stackoverflow.com/users/425465/harlam357"
      },
      "is_accepted": false,
      "score": 1,
      "last_activity_date": 1552329779,
      "creation_date": 1552329779,
      "answer_id": 55108393,
      "question_id": 55105701
    }
  ],
  "has_more": false,
  "quota_max": 10000,
  "quota_remaining": 9688
}
```

Following is a sample REST request that can be handled by the acceptAnswerById operation.

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/accept-answer](https://api.stackexchange.com/docs/accept-answer)

## Sample configuration

Following example illustrates how to connect to StackExchange with the init operation and addAnswer operation.

1. Create a sample proxy as below :
```xml
<?xml version="1.0" encoding="UTF-8"?>
<proxy xmlns="http://ws.apache.org/ns/synapse"
       name="stackexchange_addAnswer"
       startOnLoad="true"
       statistics="disable"
       trace="disable"
       transports="http,https">
   <target>
      <inSequence>
         <property expression="json-eval($.preview)" name="preview"/>
         <property expression="json-eval($.postBody)" name="postBody"/>
         <property expression="json-eval($.id)" name="id"/>
         <property expression="json-eval($.accessToken)" name="accessToken"/>
         <property expression="json-eval($.key)" name="key"/>
         <property expression="json-eval($.site)" name="site"/>
         <stackexchange.init>
            <site>{$ctx:site}</site>
            <key>{$ctx:key}</key>
            <accessToken>{$ctx:accessToken}</accessToken>
         </stackexchange.init>
         <stackexchange.addAnswer>
            <id>{$ctx:id}</id>
            <postBody>{$ctx:postBody}</postBody>
            <preview>{$ctx:preview}</preview>
         </stackexchange.addAnswer>
         <respond/>
      </inSequence>
      <outSequence/>
      <faultSequence/>
   </target>
   <description/>
</proxy>
```

2. Create a json file called `stackexchange_addAnswer.json` containing the following json:
```json
{
  "site": "<site>",
  "key": "<key>",
  "accessToken": "<access_token>",
  "id": "<question_id>",
  "postBody": "Branch prediction: With a sorted array, the condition data[c] >= 128 is first false for a streak of values, then becomes true for all later values. That's easy to predict. With an unsorted array, you pay for the branching cost",
  "preview": true
}
```

3. Replace site, key, access_token, question_id with your values.

4. Execute the following cURL command:
```
curl http://sujanan-ThinkPad-T530:8280/services/stackexchange_addAnswer -H 'Content-Type: application/json' -d @stackexchange_addAnswer.json
```

5. StackExchange will returns an json response as below :
```json
{
  "items": [
    {
      "owner": {
        "reputation": 1,
        "user_id": 55362,
        "user_type": "registered",
        "profile_image": "https://lh6.googleusercontent.com/-fxyNTmd3fOc/AAAAAAAAAAI/AAAAAAAAADw/jTw3x-DCCJs/photo.jpg?sz=128",
        "display_name": "Dhanu",
        "link": "https://stackapps.com/users/55362/dhanu"
      },
      "is_accepted": false,
      "score": 0,
      "last_activity_date": 1552540425,
      "creation_date": 1552540425,
      "answer_id": 0,
      "question_id": 8267
    }
  ],
  "has_more": false,
  "quota_max": 10000,
  "quota_remaining": 9960
}
```
