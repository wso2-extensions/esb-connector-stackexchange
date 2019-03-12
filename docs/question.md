# Working with Questions in StackExchange

[ [Overview](#overview) ]  [ [Operation details](#operation-details) ]  [ [Sample configuration](#sample-configuration) ]

### Overview
The following operations allow you to work with questions in StackExchange. Click an operation name to see details on how to use it. 
For a sample proxy service that illustrates how to work with questions, see [Sample configuration](#sample-configuration).

| Operation | Description |
| ------------- |-------------|
| [getQuestionsByUserIds](#getting-the-set-of-questions-identified-by-user-ids) | Return the set of questions identified by user ids. |
| [getQuestionsWithNoAnswers](#getting-all-the-questions-with-no-answers) | Return set of question with no answers. |
| [addQuestion](#adding-an-question) | Create a new question. |
| [deleteQuestionById](#deleting-an-question) | Delete an existing question. |
| [editQuestionById](#editing-an-question) | Edit an existing question. |
| [upvoteQuestionById](#upvoting-an-question) | Upvote an existing question. |
| [downvoteQuestionById](#downvoting-an-question) | Downvote an existing question. |

## Operation details
This section provides details on each of the operations.

### getting the set of questions identified by user ids
Return the set of questions identified by user ids.

**getQuestionsByUserIds**
```xml
<stackexchange.getQuestionsByUserIds>
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
</stackexchange.getQuestionsByUserIds>
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
  "ids": "1",
  "pagesize": 1,
  "page": 1
}
```

**Sample response**
```json
{
  "items": [
    {
      "tags": [
        "app",
        "android"
      ],
      "owner": {
        "reputation": 726,
        "user_id": 1,
        "user_type": "moderator",
        "profile_image": "https://i.stack.imgur.com/nDllk.png?s=128&g=1",
        "display_name": "Geoff Dalgas",
        "link": "https://stackapps.com/users/1/geoff-dalgas"
      },
      "is_answered": false,
      "view_count": 2692,
      "answer_count": 0,
      "score": 28,
      "last_activity_date": 1478279632,
      "creation_date": 1374695948,
      "last_edit_date": 1478279632,
      "question_id": 4240,
      "link": "https://stackapps.com/questions/4240/official-stack-exchange-android-app",
      "title": "Official Stack Exchange Android App"
    }
  ],
  "has_more": false,
  "quota_max": 300,
  "quota_remaining": 279
}
```

Following is a sample REST request that can be handled by the getQuestionsByUserIds operation.

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/questions-by-ids](https://api.stackexchange.com/docs/questions-by-ids)

### getting the set of questions identified by user ids
Return set of question with no answers.

**getQuestionsByUserIds**
```xml
<stackexchange.getQuestionsByUserIds>
    <filter>{$ctx:filter}</filter>
    <sort>{$ctx:sort}</sort>
    <page>{$ctx:page}</page>
    <max>{$ctx:max}</max>
    <fromdata>{$ctx:fromdata}</fromdata>
    <order>{$ctx:order}</order>
    <pagesize>{$ctx:pagesize}</pagesize>
    <min>{$ctx:min}</min>
    <todate>{$ctx:todate}</todate>
</stackexchange.getQuestionsByUserIds>
```

**Properties**
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
  "tagged": "rust",
  "pagesize": 1,
  "page": 1
}
```

**Sample response**
```json
{
  "items": [
    {
      "tags": [
        "elasticsearch",
        "elasticsearch-aggregation"
      ],
      "owner": {
        "reputation": 1,
        "user_id": 8700874,
        "user_type": "registered",
        "profile_image": "https://www.gravatar.com/avatar/7b3c8dd9e6a2349673be166e8eb1c5d5?s=128&d=identicon&r=PG&f=1",
        "display_name": "Saravanan",
        "link": "https://stackoverflow.com/users/8700874/saravanan"
      },
      "is_answered": false,
      "view_count": 2,
      "answer_count": 0,
      "score": 0,
      "last_activity_date": 1552358792,
      "creation_date": 1552358792,
      "question_id": 55113371,
      "link": "https://stackoverflow.com/questions/55113371/aggregations-in-elastic-search",
      "title": "Aggregations in Elastic Search"
    }
  ]
}
```

Following is a sample REST request that can be handled by the getQuestionsByUserIds operation.

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/no-answer-questions](https://api.stackexchange.com/docs/no-answer-questions)

### Adding an question
Create a new question.

**addQuestion**
```xml
<stackexchange.addQuestion>
    <title>{$ctx:title}</title>
    <tags>{$ctx:tags}</tags>
    <postBody>{$ctx:postBody}</postBody>
    <filter>{$ctx:filter}</filter>
    <preview>{$ctx:preview}</preview>
</stackexchange.addQuestion>
```

**Properties**
* title: Title of the question.
* tags: Semicolon delimited set of tags for the question.
* postBody: Body of the question.
* filter: Filter you have chosen to filter fields in the response. e.g. default
* preview: Preview parameter is used for development and testing purposes. When preview is set to true,
           request will only simulate whether it is a valid request or not and will not change
           anything on the site.

**Sample request**
```json
{
  "site": "stackoverflow",
  "title": "URLConnection does not decompress Gzip",
  "postBody": "A simple compressed response from URLConnection instance should be decompressed if Accept-Encoding headers are set correctly. But doesn't do that. Have you experienced this type of problem?",
  "tags": "java",
  "preview": true
}
```

**Sample response**
```json
{
  "items": [
    {
      "tags": [
        "java"
      ],
      "owner": {
        "reputation": 1,
        "user_id": 10965992,
        "user_type": "registered",
        "profile_image": "https://lh5.googleusercontent.com/-kVyGXuaKbiQ/AAAAAAAAAAI/AAAAAAAAAAA/ACevoQM6oMuunuZdDgfqW9iGK8svF3jnmA/mo/photo.jpg?sz=128",
        "display_name": "Bhathiya Wijesinghe",
        "link": "https://stackoverflow.com/users/10965992/bhathiya-wijesinghe"
      },
      "is_answered": false,
      "score": 0,
      "last_activity_date": 1552285161,
      "creation_date": 1552285161,
      "title": "URLConnection does not decompress Gzip"
    }
  ],
  "has_more": false,
  "quota_max": 10000,
  "quota_remaining": 9971
}
```

Following is a sample REST request that can be handled by the addQuestion operation.

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/create-question](https://api.stackexchange.com/docs/create-question)

### Deleting an question
Delete an existing question.

**deleteQuestionById**
```xml
<stackexchange.deleteQuestionById>
    <id>{$ctx:id}</id>
    <preview>{$ctx:preview}</preview>
    <filter>{$ctx:filter}</filter>
</stackexchange.deleteQuestionById>
```

**Properties**
* id: Id of the question. e.g. 8940
* filter: Filter you have chosen to filter fields in the response. e.g. default
* preview: Preview parameter is used for development and testing purposes. When preview is set to true,
           request will only simulate whether it is a valid request or not and will not change
           anything on the site.

**Sample request**
```json
{
  "site": "stackapps",
  "id": 8236,
  "preview": true
}
```

**Sample response**
```json
{
  "items": [],
  "has_more": false,
  "quota_max": 10000,
  "quota_remaining": 9967
}
```

Following is a sample REST request that can be handled by the deleteQuestion operation.

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/delete-question](https://api.stackexchange.com/docs/delete-question)

### Editing an question
Delete an existing question.

**editQuestionById**
```xml
<stackexchange.editQuestionById>
    <id>{$ctx:id}</id>
    <title>{$ctx:title}</title>
    <comment>{$ctx:comment}</comment>
    <tags>{$ctx:tags}</tags>
    <postBody>{$ctx:postBody}</postBody>
    <preview>{$ctx:preview}</preview>
    <filter>{$ctx:filter}</filter>
</stackexchange.editQuestionById>
```

**Properties**
* id: Id of the question. e.g. 8940
* title: Title of the question.
* comment: Comment about the edit.
* tags: Semicolon delimited set of tags for the question.
* postBody: Body of the question.
* filter: Filter you have chosen to filter fields in the response. e.g. default
* preview: Preview parameter is used for development and testing purposes. When preview is set to true,
           request will only simulate whether it is a valid request or not and will not change
           anything on the site.

**Sample request**
```json
{
  "site": "stackapps",
  "id": "8236",
  "title": "URLConnection does not decompress Gzip",
  "postBody": "A simple compressed response from URLConnection instance should be decompressed if Accept-Encoding headers are set correctly. But it doesn't seems to be working. Have you ever experienced this type of problem?",
  "comment": "Sample comment",
  "tags": "library;bug",
  "preview": true
}
```

**Sample response**
```json
{
  "items": [
    {
      "tags": [
        "bug",
        "library"
      ],
      "owner": {
        "reputation": 1,
        "user_id": 58519,
        "user_type": "registered",
        "profile_image": "https://lh5.googleusercontent.com/-kVyGXuaKbiQ/AAAAAAAAAAI/AAAAAAAAAAA/ACevoQM6oMuunuZdDgfqW9iGK8svF3jnmA/mo/photo.jpg?sz=128",
        "display_name": "Bhathiya Wijesinghe",
        "link": "https://stackapps.com/users/58519/bhathiya-wijesinghe"
      },
      "is_answered": false,
      "score": -1,
      "last_activity_date": 1552286273,
      "creation_date": 1550134853,
      "last_edit_date": 1552286273,
      "question_id": 8236,
      "link": "https://stackapps.com/questions/8236/urlconnection-does-not-decompress-gzip",
      "title": "URLConnection does not decompress Gzip"
    }
  ],
  "has_more": false,
  "quota_max": 10000,
  "quota_remaining": 9963
}
```

Following is a sample REST request that can be handled by the editQuestionById operation.

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/edit-question](https://api.stackexchange.com/docs/edit-question)

### Upvoting an question
Upvote an existing question.

**upvoteQuestionById**
```xml
<stackexchange.upvoteQuestionById>
    <id>{$ctx:id}</id>
    <preview>{$ctx:preview}</preview>
    <filter>{$ctx:filter}</filter>
</stackexchange.upvoteQuestionById>
```

**Properties**
* id: Id of the question. e.g. 8940
* filter: Filter you have chosen to filter fields in the response. e.g. default
* preview: Preview parameter is used for development and testing purposes. When preview is set to true,
           request will only simulate whether it is a valid request or not and will not change
           anything on the site.

**Sample request**

Following is a sample REST request that can be handled by the upvoteQuestionById operation.

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/upvote-question](https://api.stackexchange.com/docs/upvote-question)

### Downvoting an question
Downvote an existing question.

**downvoteQuestionById**
```xml
<stackexchange.downvoteQuestionById>
    <id>{$ctx:id}</id>
    <preview>{$ctx:preview}</preview>
    <filter>{$ctx:filter}</filter>
</stackexchange.downvoteQuestionById>
```

**Properties**
* id: Id of the question. e.g. 8940
* filter: Filter you have chosen to filter fields in the response. e.g. default
* preview: Preview parameter is used for development and testing purposes. When preview is set to true,
           request will only simulate whether it is a valid request or not and will not change
           anything on the site.

**Sample request**

Following is a sample REST request that can be handled by the downvoteQuestionById operation.

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/downvote-question](https://api.stackexchange.com/docs/downvote-question)