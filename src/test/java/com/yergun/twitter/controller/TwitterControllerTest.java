package com.yergun.twitter.controller;

import com.yergun.twitter.model.Message;
import com.yergun.twitter.model.User;
import com.yergun.twitter.service.TwitterService;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;


@WebFluxTest
@RunWith(SpringRunner.class)
public class TwitterControllerTest {

    @MockBean
    private TwitterService twitterService;

    @Autowired
    private WebTestClient client;

    private static User TEST_USER;

    @BeforeClass
    public static void init() {
        TEST_USER = new User(1L, "user", "yolo", 666L, null);
        TEST_USER.addMessage(new Message(2L, 999L, "for whatev reason"));
    }

    @Test
    public void filterTweets() throws Exception {
        Mockito.when(twitterService.filterTweets("whatev"))
                .thenReturn(Flux.just(TEST_USER));

        this.client.get().uri("https://localhost:8080/twitter/filter?track=whatev")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_STREAM_JSON_VALUE + ";charset=UTF-8")
                .expectBody()
                .jsonPath("@.id").isEqualTo(TEST_USER.getId())
                .jsonPath("@.name").isEqualTo(TEST_USER.getName())
                .jsonPath("@.screen_name").isEqualTo(TEST_USER.getScreenName())
                .jsonPath("@.created_at").isEqualTo(TEST_USER.getCreatedAt())
                .jsonPath("@.messages[0].id").isEqualTo(TEST_USER.getMessages().iterator().next().getId())
                .jsonPath("@.messages[0].created_at").isEqualTo(TEST_USER.getMessages().iterator().next().getCreatedAt())
                .jsonPath("@.messages[0].text").isEqualTo(TEST_USER.getMessages().iterator().next().getText());
    }

    @Test
    public void report() {
        Mockito.when(twitterService.findAllUsersSortedByCreationDate())
                .thenReturn(Flux.just(TEST_USER));

        this.client.get().uri("https://localhost:8080/twitter/report")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE + ";charset=UTF-8")
                .expectBody()
                .jsonPath("@.[0].id").isEqualTo(TEST_USER.getId())
                .jsonPath("@.[0].name").isEqualTo(TEST_USER.getName())
                .jsonPath("@.[0].screen_name").isEqualTo(TEST_USER.getScreenName())
                .jsonPath("@.[0].created_at").isEqualTo(TEST_USER.getCreatedAt())
                .jsonPath("@.[0].messages[0].id").isEqualTo(TEST_USER.getMessages().iterator().next().getId())
                .jsonPath("@.[0].messages[0].created_at").isEqualTo(TEST_USER.getMessages().iterator().next().getCreatedAt())
                .jsonPath("@.[0].messages[0].text").isEqualTo(TEST_USER.getMessages().iterator().next().getText());
    }
}
