# Twitter Filter Application

### Features
Application is asynchronous end to end which includes the following reactive tech stack:
- Spring Boot
- Spring Webflux
- Spring Data Reactive Mongo
- Spring WebClient

### How to run

Set the properties (can be obtained from an app settings in twitter):
```
twitter:
  api:
    consumer-key: ${TWITTER_APP_CONSUMER_KEY}
    consumer-secret: ${TWITTER_APP_CONSUMER_SECRET}
    access-token: ${TWITTER_APP_ACCESS_TOKEN}
    access-token-secret: ${TWITTER_APP_ACCESS_TOKEN_SECRET}
```
If Docker is installed, application can be built and run with the following script:
```
./start.sh 
```
which is just a shortcut for:
1. mvn clean package
2. docker-compose build
3. docker-compose up

Application can also be started as a standalone spring boot app, in this case there should be a mongodb instance running on `localhost:27017`.

## How to use
- `http://localhost:8080/twitter/filter` to start the real time filtering for the word `bieber`
- Track word can be overriden like the following: `http://localhost:8080/twitter/filter?track=ajax`
- This will update the browser on every captured tweet
- Every tweet is transformed and saved to mongo on the fly and without blocking the stream
- Stream connection is closed after 30 messages or 100 seconds.
- `http://localhost:8080/twitter/report` to load the data in the format of `tweets grouped by user`(can be run during the stream or after)

## Tests & Coverage
Tests for `TwitterClient` is using WireMock via `spring-cloud-contract-wiremock`

Jacoco plugin is attached to maven test goal. After tests are run,  report can be found here:
`target/site/jacoco/index.html`