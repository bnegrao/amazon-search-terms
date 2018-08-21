# amazon-search-terms

## API endpoints

### GET /estimate
**Description:** Returns the `score` of a keyword. The `score` is simply the number of search terms retrieved from the amazon complete api that have the keyword given as prefix.  
**Query Params:**   
* `keyword`: REQUIRED. The term to be searched. 
* `timeoutMilis`: OPTIONAL. The timeout value in miliseconds to wait for the results. Default: 10000 (10 seconds).
* `showSearchResults`: OPTIONAL. When true, shows the list of terms found, without pagination. Default: false. 
**Response Codes:** 200 - Success.  
**Response Data:** a json containing the score found.  

### Examples

#### Query:

`http://localhost:8080/estimate?keyword=iphone`

#### Response:
```
{
    "keyword": "iphone",
    "score": 1924,
    "timeElapsedMilis": 10354,
    "searchTerms": []
}
```
#### Query:

`http://localhost:8080/estimate?keyword=iphone 8 case black leather&timeoutMilis=10000&showSearchTerms=true`

#### Response:
```
{
    "keyword": "iphone 8 case black leather",
    "score": 2,
    "timeElapsedMilis": 652,
    "searchTerms": [
        "iphone 8 case black leather",
        "iphone 8 case black leather wallet"
    ]
}
```

## Implementation Details
