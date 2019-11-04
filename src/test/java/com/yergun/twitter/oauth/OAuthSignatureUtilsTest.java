package com.yergun.twitter.oauth;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.ClientRequest;

import java.net.URI;

public class OAuthSignatureUtilsTest {

    private final OAuthSignatureUtils oauth = new OAuthSignatureUtils(
            new OAuthCredentials("a","b","c","d"));

    @Test
    public void testOAuthHeaderFields() {
        String oauthString = oauth.oAuth1Header(ClientRequest.create(HttpMethod.GET, URI.create("https://stream.twitter.com/1.1")).build());
        Assertions.assertThat(oauthString)
                .startsWith("OAuth")
                .contains("oauth_signature")
                .contains("oauth_nonce")
                .contains("oauth_timestamp")
                .contains("oauth_token")
                .contains("oauth_consumer_key")
                .contains("oauth_signature_method")
                .contains("oauth_version");
    }
}
