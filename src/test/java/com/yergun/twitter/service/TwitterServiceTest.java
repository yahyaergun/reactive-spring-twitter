package com.yergun.twitter.service;

import com.yergun.twitter.client.TwitterClient;
import com.yergun.twitter.client.dto.TweetDTO;
import com.yergun.twitter.client.dto.UserDTO;
import com.yergun.twitter.model.Message;
import com.yergun.twitter.model.User;
import com.yergun.twitter.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.mockito.ArgumentMatchers.*;


@RunWith(SpringRunner.class)
@Import(TwitterService.class)
public class TwitterServiceTest {

    @MockBean
    private TwitterClient twitterClient;
    @MockBean
    private UserRepository userRepository;

    @Autowired
    private TwitterService twitterService;

    private final static Long EPOCH = 1569588335L;
    private final static String FIRST_TWEET_TEXT = "first tweet text";
    private final static String SECOND_TWEET_TEXT = "second tweet text";
    private final static String USER_NAME = "user";
    private final static String SCREEN_NAME = "screen";
    private final static Long FIRST_TWEET_ID = 11L;
    private final static Long SECOND_TWEET_ID = 1111L;
    private final static Long USER_ID = 22L;

    private User EXPECTED_USER_FIRST;
    private User EXPECTED_USER_SECOND;

    @Before
    public void init() {
        // prepare input objects
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(EPOCH), ZoneId.of("UTC+0"));
        UserDTO userDTO = new UserDTO(USER_ID, USER_NAME, SCREEN_NAME, zonedDateTime);
        TweetDTO firstTweet = new TweetDTO(FIRST_TWEET_ID, zonedDateTime, FIRST_TWEET_TEXT, userDTO);
        TweetDTO secondTweet = new TweetDTO(SECOND_TWEET_ID, zonedDateTime, SECOND_TWEET_TEXT, userDTO);

        Mockito.when(twitterClient.filterTweets("One user one message")).thenReturn(Flux.just(firstTweet));
        Mockito.when(twitterClient.filterTweets("One user two message")).thenReturn(Flux.just(firstTweet, secondTweet));

        // prepare output user object
        Message firstMessage = new Message(FIRST_TWEET_ID, EPOCH, FIRST_TWEET_TEXT);
        Message secondMessage = new Message(SECOND_TWEET_ID, EPOCH+1000, SECOND_TWEET_TEXT);

        EXPECTED_USER_FIRST = new User(USER_ID, USER_NAME, SCREEN_NAME, EPOCH, null);
        EXPECTED_USER_FIRST.addMessage(firstMessage);

        EXPECTED_USER_SECOND = new User(USER_ID, USER_NAME, SCREEN_NAME, EPOCH, null);
        EXPECTED_USER_SECOND.addMessage(firstMessage).addMessage(secondMessage);

        Mockito.when(userRepository.save(any(User.class)))
                .thenReturn(Mono.just(EXPECTED_USER_FIRST)) //first
                .thenReturn(Mono.just(EXPECTED_USER_SECOND)); //second

        Mockito.when(userRepository.findById(USER_ID))
                .thenReturn(Mono.empty()) //first
                .thenReturn(Mono.just(EXPECTED_USER_FIRST)); //second

        // findAll
        Mockito.when(userRepository.findAll(any(Sort.class)))
                .thenReturn(Flux.just(EXPECTED_USER_FIRST));
    }

    @Test
    public void filterTweets_testTransformation_OneTweetToAUserAndOneMessage() {
        Flux<User> tweets = twitterService.filterTweets("One user one message");

        StepVerifier.create(tweets)
                .expectNext(EXPECTED_USER_FIRST)
                .verifyComplete();
    }

    @Test
    public void filterTweets_testTransformation_twoTweetsToAUserAndTwoMessage() {
        Flux<User> tweets = twitterService.filterTweets("One user two message");

        StepVerifier.create(tweets)
                .expectNext(EXPECTED_USER_FIRST)
                .expectNext(EXPECTED_USER_SECOND)
                .verifyComplete();
    }

    @Test
    public void findAll() {
        Flux<User> allUsers = twitterService.findAllUsersSortedByCreationDate();

        StepVerifier.create(allUsers)
                .expectNext(EXPECTED_USER_FIRST)
                .verifyComplete();
    }



}
