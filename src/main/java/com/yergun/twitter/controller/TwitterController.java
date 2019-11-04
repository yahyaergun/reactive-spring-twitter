package com.yergun.twitter.controller;

import com.yergun.twitter.service.TwitterService;
import com.yergun.twitter.util.TwitterUtils;
import lombok.RequiredArgsConstructor;
import com.yergun.twitter.model.User;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/twitter")
@RequiredArgsConstructor
public class TwitterController {

    private final TwitterService twitterService;

    @GetMapping(value = "/filter", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<User> filterTweets(@RequestParam(value = "track", defaultValue = TwitterUtils.QUERY_PARAM_FILTER_DEFAULT_VALUE) String trackValue) {
        return twitterService.filterTweets(trackValue);
    }

    @GetMapping(value = "/report", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Flux<User> report() {
        return twitterService.findAllUsersSortedByCreationDate();
    }

}
