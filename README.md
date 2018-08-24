# amazon-search-terms

## API endpoints

### GET /estimate
**Description:** Returns the `score` of a keyword from 0 to 100, indicating how frequent the keyword is searched on amazon.com. 0 means the keyword was not found in amazon.com, while 100 means the keyword is extremely searched. The estimation will take until 10 seconds to return, it uses a recursive search algorithm to estimate the popularity of a given keyword. See the section *Implementation Details* bellow for more details on how the estimation is calculated.

**Query Params:**   
* `keyword`: REQUIRED. The term to be searched. 

**Response Codes:** 200 - Success.    

**Response Data:** a json containing the score estimated.  

### Examples

#### Query:

`http://localhost:8080/estimate?keyword=iphone`

#### Response:
```
{
    "keyword": "iphone",
    "score": 100
}
```
#### Query:

`http://localhost:8080/estimate?keyword=gloria  bucco`

#### Response:
```
{
    "keyword": "gloria bucco",
    "score": 1
}
```

#### Query:

`http://localhost:8080/estimate?keyword=canon`

#### Response:
```
{
    "keyword": "canon",
    "score": 78
}
```

### GET /search
**Description:** this endpoint brings statistics from the search algorithm. 

**Query Params:**   
* `keyword`: REQUIRED. The term to be searched.  
* `timeoutMilis`: OPTIONAL. The timeout to interrupt the search, in milliseconds. Default is 10.000 (10s). The search can return before the timeout if the keyword is unfrequent.  

**Response Codes:** 200 - Success.    

**Response Data:** a json containing the statistics from the search.

### Examples

#### Query

`http://localhost:8080/search?keyword=iphone plastic screen`

#### Response

```
{
    "numberOfKeywordsFound": 10,
    "numberOfRequests": 38,
    "numberOfRequestsWithNoResults": 26,
    "searchInterruptedDueToTimeout": false,
    "keywordsNotSearchedDueToTimeout": 0,
    "timeElapsedMilis": 1941,
    "keywordsList": [
        "iphone plastic screen",
        "iphone plastic screen cover",
        "iphone plastic screen protector",
        "iphone plastic screen protector 6",
        "iphone plastic screen protector 6 plus",
        "iphone plastic screen protector 6s",
        "iphone plastic screen protector 7",
        "iphone plastic screen protector 7 plus",
        "iphone plastic screen protector 8",
        "iphone plastic screen protector 8 plus"
    ]
}
```

The search above ended in just 1941 miliseconds (2 seconds), the keyword "iphone plastic screen" brought only 10 descendant search terms only. 38 requests were sent to the amazon completion api, but 26 raised no results.

#### Query

`http://localhost:8080/search?keyword=iphone&timeoutMilis=10000`

#### Response

```
{
    "numberOfKeywordsFound": 2094,
    "numberOfRequests": 235,
    "numberOfRequestsWithNoResults": 0,
    "searchInterruptedDueToTimeout": true,
    "keywordsNotSearchedDueToTimeout": 2004,
    "timeElapsedMilis": 10005,
    "keywordsList": [
        "apple iphone charger cable 6ft",
        "apple iphone charger with wall plug",
        "iphone 0",
        "iphone 0 case",
        "iphone 0.99",
        . . .  ( more than 2000 items found )
     ]
}
```` 

In 10 seconds 'iphone' brought 2094 descendent keywords. The app sent 235 requests to amazon and all of them returned results as was indicated by `"numberOfRequestsWithNoResults": 0,` .

## Implementation Details

### The score calculation algorithm

In order to create the scale from '0' to '100', it was necessary to establish a parameter for what would be the score of '100'. The most popular keyword I found was *iphone*, it returns more than 2000 related search terms in within ten seconds. No other term that I tryed could surpass that amount.

So if one keyword brings >= 2000 terms within 10 seconds, it will gain score '100', bellow 2000 is applied a simple [proportional calculation](http://www.math.com/school/subject1/lessons/S1U2L2DP.html), for example, 1000 terms returned in 10 seconds is score '50', 200 terms is '10'.

The 2000 keywords result is dependent of my machine power and my internet link speed. If your machine performs much better you could increase the 2000 limit using the parameter `estimation.number.of.keywords.upper.limit` in the `application.properties` file.


### The search algorithm

The algorithm sends requests in parallel to speed up the searches. 

To start, the code will query the Amazon Completion API and retrive the "top ten" results, it will then start to search recursively for each of the top ten results for more sub terms. More than that, the code will apend characters from a-z0-9 at the end of each term found in order to find more results that are not in the "top ten" list. 

Look at the class `com.bnegrao.amazonsearchterms.service.AmazonAPIService`


## How to execute the app
Download the code from github and at the project root folder run the command bellow:

    mvn spring-boot:run

That will run the app in foreground, listening on port 8080. Log messages will be printed out to the console.
