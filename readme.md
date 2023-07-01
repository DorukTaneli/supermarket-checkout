
# supermarket-checkout

Spring boot application for supermarket checkout.

## Requirements

The full requirements can be found in Requirements.pdf.
Here is a short summary for this supermarket checkout application:

- Support multiple clients.
- Set pricing rules each time you start a checkout.
- Items are identified by using Stock Keeping Units (SKU). Individual letters of the alphabet will be used as SKU. (e.g. A, B, C)
- Scan items in any order. If we scan a B, an A and another B, it will recognize the two B's and apply the according pricing rule automatically.
- Calculate total price correctly with special offer pricing.

Main steps when using the API:

1. Start a checkout and get checkoutid
2. Set pricing rules
3. Scan an arbitrary number of items
4. Calculate the total price

## Before first run
- Ensure Docker desktop is installed.
- Run `docker-compose -f docker-compose.yaml up -d` to start MongoDB container.

## How to use

1. Start checkout: GET /checkout/start 

returns checkoutId. Ex: `649ffcc0268ec86f8cfe36af`

2. Send pricing rules: POST /checkout/{checkoutId}/pricing

Request Body in JSON:
```
[
    {
        "sku": "A",
        "price": 40,
        "specialOffer": {
            "count": 3,
            "price": 100
        }
    },
    {
        "sku": "B",
        "price": 50
    }
]
```

3. Scan items: POST checkout/{checkoutId}/scan

Request Body in JSON:
```
{
    "sku": "A"
}
```

4. Get total price: GET checkout/{checkoutId}/total

returns total price. Ex: `305`

## Project structure

- SupermarketCheckoutApplication: Starts Spring Boot Application
- CheckoutController: Handles API requests
- CheckoutService: Handles business logic
- CheckoutRepository: Handles Database methods
- internal: Internal classes
  - Checkout
      - int checkoutId
      - List pricingList
      - List scanList
  - Pricing
      - char sku
      - int price
      - SpecialOffer specialOffer
  - SpecialOffer
      - int count
      - int price
  - Scan
      - char sku
      - int count
- requestbody
  - ScanRequestBody: Class to deserialize scan request bodies
- test/java/...integration
  - IntegrationTests: Sends http requests as explained in How to use section above, to test the application as a whole.

## MongoDB

After running docker-compose, MongoDB database can be viewed by via Mongo Express on http://localhost:8081/.

## Further Improvements

- POST Request Responses
  - On a successful POST request, the system only returns HTTP 200 with no additional info. Request responses can be improved.
- Error Responses
  - If there is an error, the system returns HTTP 500. Error responses can be improved by using relevant HTTP Error codes.
- Remaining CRUD operations
  - The endpoints only support the operations listed in how to use. Remaining CRUD operations can be added.
- No security
  - Everyone can make changes to the pricing/scan of a checkout if they know the checkoutID. There is no auth flow.
- No unit testing
  - There is only integration test, no unit tests.
- Documentation
  - Only checkoutService has JavaDoc documentation. 

## Architecture Decision Record - Use MongoDB

### Context and Problem Statement
Which database should be used?

### Considered Options
- MongoDB
- PostgreSQL: Considered a relational database like PostgreSQL as Spring Data JPA is very powerful, 
and Job Description mentions SQL databases.

### Decision
Chosen Option: MongoDB. According to the requirements, the data structure suits a document database better.
The pricing and scan info belongs to a single checkout, which can be stored in that checkout's document.
Plus, NoSQL Databases are often used for real-world shopping cart implementations.
