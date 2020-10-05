round-up
========

Problem Statement
========
For a customer, take all the transactions in a given week and round them up to the
nearest pound. For example with spending of £4.35, £5.20 and £0.87, the round-up
would be £1.58. This amount should then be transferred into a savings goal, helping the
customer save for future adventures.

Rest Api endpoint for fetching transactions between two timestamps and rounding up the pennies and depositing them to savings-goal

Method ```PUT```

URL - ```http://localhost:7777/roundup/transactions-between?minTransactionTimestamp=:minTransactionTimestamp&maxTransactionTimestamp=:maxTransactionTimestamp```
Timestamp format = ```yyyy-MM-ddTHH:mm:SS.sssZ```

request body - ```{
                           "savingsGoal": {
                             "name": "Trip to Paris",
                             "currency": "GBP",
                             "target": {
                               "currency": "GBP",
                               "minorUnits": 123456
                             },
                             "base64EncodedPhoto": "string"
                           }
                         }```
                         


The end user gets to pass the saving goal he wants to create as request body and transaction period as query params

Validation for timestamp
   *  difference in timestamp must be equal to 7 days ( 1 week)
   *  minTransactionTimestamp must be before maxTransactionTimestamp 
   *  timestamps should be in the format ```yyyy-MM-dd'T'HH:mm:ss.SSSz```

Sample request body - 
    ```{
         "savingsGoal": {
           "name": "Trip to Paris",
           "currency": "GBP",
           "target": {
             "currency": "GBP",
             "minorUnits": 123456
           },
           "base64EncodedPhoto": "string"
         }
       }```

Flow -

get list of customer Accounts 
  1. check if customer has atleast 1 GBP account, 
        if no GBP accounts then throw AccountUnavailableException
        else fetch account Id and create a saving goal using the request body
 
  2. check if saving goal is created successfully 
        if it hasn't been successfully created, throw SavingsGoalException
        else fetch savings Uid
 
  3. with the accountId and query parameters provided as part of the request, fetch all transactions for the time period
  4. round up the (OUT) transactions in the feed to its nearest pound
  5. create a transferUid
  6. create deposit saving goal request, deposit rounded up value computed in step 4 and return a 200 if successful, else throw SavingsGoalException
  
StarlingClientExceptions are thrown when an unsuccessful response is returned when requesting Starling's open api and validation errors from starling's api are propogated in the application and returned in response body with relevant response codes

Sample Response:
```{
       "transferUid": "4b898d01-aeed-4415-9adf-1d9c059680cd",
       "savingsGoalUid": "2cf55908-e99d-4cb2-9078-1b686d9b359a",
       "transferAmount": {
           "currency": "GBP",
           "minorUnits": 1600
       }
   }```
    
                                  

