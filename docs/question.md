# Working with Questions in StackExchange

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

### Adding an question
**addQuestion**
```xml
<stackexchange.addQuestion>
    <title>{$ctx:title}</title>
    <site>{$ctx:site}</site>
    <tags>{$ctx:tags}</tags>
    <postBody>{$ctx:postBody}</postBody>
    <filter>{$ctx:filter}</filter>
    <preview>{$ctx:preview}</preview>
</stackexchange.addQuestion>
```

**Properties**
* id: Id of the question to add an question. e.g. 8940

**Sample request**

Following is a sample REST request that can be handled by the addQuestion operation.

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/create-question](https://api.stackexchange.com/docs/create-question)

### Deleting an question
**deleteQuestionById**
```xml
<stackexchange.deleteQuestionById>
    <id>{$ctx:id}</id>
    <preview>{$ctx:preview}</preview>
    <filter>{$ctx:filter}</filter>
</stackexchange.deleteQuestionById>
```

**Properties**
* ids: Id of the question. e.g. 8940

**Sample request**

Following is a sample REST request that can be handled by the deleteQuestion operation.

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/delete-question](https://api.stackexchange.com/docs/delete-question)

### Editing an question
**editQuestionById**
```xml
<stackexchange.editQuestionById>
    <id>{$ctx:id}</id>
    <postBody>{$ctx:postBody}</postBody>
    <preview>{$ctx:preview}</preview>
    <filter>{$ctx:filter}</filter>
</stackexchange.editQuestionById>
```

**Properties**
* ids: Id of the question. e.g. 8940

**Sample request**

Following is a sample REST request that can be handled by the editQuestionById operation.

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/edit-question](https://api.stackexchange.com/docs/edit-question)

### Upvoting an question
**upvoteQuestionById**
```xml
<stackexchange.upvoteQuestionById>
    <id>{$ctx:id}</id>
    <preview>{$ctx:preview}</preview>
    <filter>{$ctx:filter}</filter>
</stackexchange.upvoteQuestionById>
```

**Properties**
* ids: Id of the question. e.g. 8940

**Sample request**

Following is a sample REST request that can be handled by the upvoteQuestionById operation.

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/upvote-question](https://api.stackexchange.com/docs/upvote-question)

### Downvoting an question
**downvoteQuestionById**
```xml
<stackexchange.downvoteQuestionById>
    <id>{$ctx:id}</id>
    <preview>{$ctx:preview}</preview>
    <filter>{$ctx:filter}</filter>
</stackexchange.downvoteQuestionById>
```

**Properties**
* ids: Id of the question. e.g. 8940

**Sample request**

Following is a sample REST request that can be handled by the downvoteQuestionById operation.

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/downvote-question](https://api.stackexchange.com/docs/downvote-question)

### Accepting an question
**acceptQuestionById**
```xml
<stackexchange.acceptQuestionById>
    <id>{$ctx:id}</id>
    <preview>{$ctx:preview}</preview>
    <filter>{$ctx:filter}</filter>
</stackexchange.acceptQuestionById>
```

**Properties**
* ids: Id of the question. e.g. 8940

**Sample request**

Following is a sample REST request that can be handled by the acceptQuestionById operation.

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/accept-question](https://api.stackexchange.com/docs/accept-question)
