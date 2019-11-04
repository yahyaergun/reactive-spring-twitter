package com.yergun.twitter.client;

import com.yergun.twitter.client.dto.TweetDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import wiremock.org.apache.http.HttpHeaders;
import wiremock.org.apache.http.HttpStatus;

import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureWireMock
@ActiveProfiles("test")
public class TwitterClientTest {

    @Autowired
    private TwitterClient twitterClient;

    @Before
    public void setupWireMock() {
        stubFor(get("/stream/wiremock?track=bieber")
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .withStatus(HttpStatus.SC_OK)
                        .withBodyFile("twitter_dump_60_messages.json")));
    }

    @Test
    public void filterTweets_atMost30Tweets() throws Exception {
        Flux<TweetDTO> tweets = twitterClient.filterTweets("bieber");

        StepVerifier.create(tweets)
                .expectNextCount(30)
                .verifyComplete();
    }

    // Can not test take(Duration) on the twitterClient itself
    // as it is leaving the current JVM and shooting the request,
    // so it doesn't obey the rules of time travel provided by withVirtualTime
    @Test
    public void takeDuration100_closesConnectionAfter100Seconds() {
        StepVerifier
                .withVirtualTime(() -> Flux.interval(
                        Duration.ofSeconds(66)).take(5).take(Duration.ofSeconds(100))) // One element every 66 secs
                .thenAwait(Duration.ofSeconds(100))
                .expectNextCount(1) // after 100 second we should have only 1 element.
                .verifyComplete();
    }
}
