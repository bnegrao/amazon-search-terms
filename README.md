# amazon-search-terms

## API endpoints

### GET /estimate
**Description:** Returns the `score` of a keyword after a recursive search of the term on the Amazon Completion API. The `score` is the exact number of search terms retrieved from Amazon and having the keyword given as prefix. See the section *Implementation Details* bellow for more details on the search algorithm.

**Query Params:**   
* `keyword`: REQUIRED. The term to be searched. 
* `timeoutMilis`: OPTIONAL. The timeout value in miliseconds to wait for the results. Default: 10000 (10 seconds).
* `showSearchResult`: OPTIONAL. When true, shows the list of terms found, without pagination. Default: false.  

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
}
```
#### Query:

`http://localhost:8080/estimate?keyword=iphone 8 case black leather&timeoutMilis=10000&showSearchResult=true`

#### Response:
```
{
    "keyword": "iphone 8 case black leather",
    "score": 2,
    "timeElapsedMilis": 652,
    "searchResult": [
        "iphone 8 case black leather",
        "iphone 8 case black leather wallet"
    ]
}
```
## How to execute the app
Download the code from github and at the project root folder run the command bellow:

    mvn spring-boot:run

That will run the app in foreground, listening on port 8080. Log messages will be printed out to the console.


## Implementation Details

This implementation is focused in finding the maximum amount of search terms within the 'timeoutMilis' interval. The 'score' value is not an estimation, instead it is the exact number of search terms found with the given 'keyword' prefix.

The algorithm sends requests in parallel to speed up the searches. 

To start, the code will query the Amazon Completion API and retrive the "top ten" results, it will then start to search recursively for each of the top ten results for more sub terms. More than that, the code will apend characters from a-z0-9 at the end of each term found in order to find more results that are not in the "top ten" list. 

Look at the class `com.bnegrao.amazonsearchterms.service.AmazonAPIService`
