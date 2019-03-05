# Working with Answers in StackExchange

### Overview
The following operations allow you to work with answers in StackExchange. Click an operation name to see details on how to use it. 
For a sample proxy service that illustrates how to work with answers, see [Sample configuration](#sample-configuration).

| Operation | Description |
| ------------- |-------------|
| [getAnswersByIds](#getting-the-set-of-answers-identified-by-ids) | Return the set of answers identified by ids. |
| [addAnswer](#adding-an-answer) | Create a new answer. |
| [deleteAnswer](#deleting-an-answer) | Delete an existing answer. |
| [editAnswer](#editing-an-answer) | Edit an existing answer. |
| [upvoteAnswer](#upvoting-an-answer) | Upvote an existing answer. |
| [downvoteAnswer](#downvoting-an-answer) | Downvote an existing answer. |
| [acceptAnswer](#Accepting-an-answer) | Accept an existing answer. |

## Operation details
This section provides details on each of the operations.

### getting the set of answers identified by ids
**getAnswersByIds**
```xml
<stackexchange.getAnswersByIds>
    <ids>{$ctx:ids}</ids>
</stackexchange.getAnswersByIds>
```

**Properties**
* ids: Semicolon delimited answer ids. e.g. 1;2;3

**Sample request**

Following is a sample REST request that can be handled by the getAnswersByIds operation.
```xml
<stackexchange.getAnswersByIds>
    <ids>1;2;3</ids>
</stackexchange.getAnswersByIds>
```

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/answers-by-ids](https://api.stackexchange.com/docs/answers-by-ids)

### Adding an answer
**addAnswer**
```xml
<stackexchange.addAnswer>
    <id>{$ctx:id}</id>
    <body>{$ctx:body}</body>
</stackexchange.addAnswer>
```

**Properties**
* id: Id of the question to add an answer. e.g. 8940

**Sample request**

Following is a sample REST request that can be handled by the addAnswer operation.
```xml
<stackexchange.addAnswer>
    <key>VnXXXXXXXXXXXXXXXXXXXXXX</key>
    <site>stackoverflow</site>
    <accessToken>7rXXXXXXXXXXXXXXXXXXXXXX</accessToken>
    <id>8940</id>
    <preview>true</preview>
    <body>It's an answer. A terrible answer, but an answer nonetheless</body>
</stackexchange.addAnswer>
```

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/create-answer](https://api.stackexchange.com/docs/create-answer)

### Deleting an answer
**deleteAnswerById**
```xml
<stackexchange.deleteAnswerById>
    <id>{$ctx:id}</id>
</stackexchange.deleteAnswerById>
```

**Properties**
* ids: Id of the answer. e.g. 8940

**Sample request**

Following is a sample REST request that can be handled by the deleteAnswer operation.
```xml
<stackexchange.deleteAnswerById>
    <key>VnXXXXXXXXXXXXXXXXXXXXXX</key>
    <site>stackoverflow</site>
    <accessToken>7rXXXXXXXXXXXXXXXXXXXXXX</accessToken>
    <id>9840</id>
    <preview>true</preview>
</stackexchange.deleteAnswerById>
```

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/delete-answer](https://api.stackexchange.com/docs/delete-answer)

### Editing an answer
**editAnswerById**
```xml
<stackexchange.editAnswerById>
    <id>{$ctx:id}</id>
    <body>{$ctx:body}</body>
</stackexchange.editAnswerById>
```

**Properties**
* ids: Id of the answer. e.g. 8940

**Sample request**

Following is a sample REST request that can be handled by the editAnswerById operation.
```xml
<stackexchange.editAnswerById>
    <key>VnXXXXXXXXXXXXXXXXXXXXXX</key>
    <site>stackoverflow</site>
    <accessToken>7rXXXXXXXXXXXXXXXXXXXXXX</accessToken>
    <id>9840</id>
    <preview>true</preview>
    <body>It's an answer. A terrible answer, but an answer nonetheless</body>
</stackexchange.editAnswerById>
```

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/edit-answer](https://api.stackexchange.com/docs/edit-answer)

### Upvoting an answer
**upvoteAnswerById**
```xml
<stackexchange.upvoteAnswerById>
    <id>{$ctx:id}</id>
</stackexchange.upvoteAnswerById>
```

**Properties**
* ids: Id of the answer. e.g. 8940

**Sample request**

Following is a sample REST request that can be handled by the upvoteAnswerById operation.
```xml
<stackexchange.upvoteAnswerById>
    <key>VnXXXXXXXXXXXXXXXXXXXXXX</key>
    <site>stackoverflow</site>
    <accessToken>7rXXXXXXXXXXXXXXXXXXXXXX</accessToken>
    <id>9840</id>
    <preview>true</preview>
</stackexchange.upvoteAnswerById>
```

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/upvote-answer](https://api.stackexchange.com/docs/upvote-answer)

### Downvoting an answer
**downvoteAnswerById**
```xml
<stackexchange.downvoteAnswerById>
    <id>{$ctx:id}</id>
</stackexchange.downvoteAnswerById>
```

**Properties**
* ids: Id of the answer. e.g. 8940

**Sample request**

Following is a sample REST request that can be handled by the downvoteAnswerById operation.
```xml
<stackexchange.downvoteAnswerById>
    <key>VnXXXXXXXXXXXXXXXXXXXXXX</key>
    <site>stackoverflow</site>
    <accessToken>7rXXXXXXXXXXXXXXXXXXXXXX</accessToken>
    <id>9840</id>
    <preview>true</preview>
</stackexchange.upvoteAnswerById>
```

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/downvote-answer](https://api.stackexchange.com/docs/downvote-answer)

### Accepting an answer
**acceptAnswerById**
```xml
<stackexchange.acceptAnswerById>
    <id>{$ctx:id}</id>
</stackexchange.acceptAnswerById>
```

**Properties**
* ids: Id of the answer. e.g. 8940

**Sample request**

Following is a sample REST request that can be handled by the acceptAnswerById operation.
```xml
<stackexchange.acceptAnswerById>
    <key>VnXXXXXXXXXXXXXXXXXXXXXX</key>
    <site>stackoverflow</site>
    <accessToken>7rXXXXXXXXXXXXXXXXXXXXXX</accessToken>
    <id>9840</id>
    <preview>true</preview>
</stackexchange.acceptAnswerById>
```

**Related StackExchange API documentation**
[https://api.stackexchange.com/docs/accept-answer](https://api.stackexchange.com/docs/accept-answer)
