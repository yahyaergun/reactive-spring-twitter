package com.yergun.twitter.oauth;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class OAuthCredentials {

    private final String consumerKey;
    private final String consumerSecret;
    private final String accessToken;
    private final String accessTokenSecret;

    public OAuthCredentials(@Value("${twitter.api.consumer-key}") String consumerKey,
                            @Value("${twitter.api.consumer-secret}") String consumerSecret,
                            @Value("${twitter.api.access-token}") String accessToken,
                            @Value("${twitter.api.access-token-secret}") String accessTokenSecret) {

        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
    }
}
