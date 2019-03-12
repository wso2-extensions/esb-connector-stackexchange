# Working with Users in StackExchange

[ [Overview](#overview) ]  [ [Operation details](#operation-details) ]  [ [Sample configuration](#sample-configuration) ]

### Overview
The following operations allow you to work with users in StackExchange. Click an operation name to see details on how to use it. 
For a sample proxy service that illustrates how to work with users, see [Sample configuration](#sample-configuration).

| Operation | Description |
| ------------- |-------------|
| [getMe](#getting-the-information-about-yourself) | Return the information about yourself. |

## Operation details
This section provides details on each of the operations.

### Getting the information about yourself

**Sample request**

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