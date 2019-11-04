package com.yergun.twitter.client;

import com.yergun.twitter.oauth.OAuthSignatureUtils;
import lombok.extern.log4j.Log4j2;
import com.yergun.twitter.client.dto.TweetDTO;
import com.yergun.twitter.util.TwitterUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@Log4j2
public class TwitterClient {

    @Value("${twitter.api.filter-url}")
    private String filterUrl;
    private WebClient webClient;

    public TwitterClient(WebClient.Builder webClientBuilder,
                         OAuthSignatureUtils oAuthSignatureUtils,
                         @Value("${twitter.api.stream-base-url}") String baseStreamUrl) {
        this.webClient = webClientBuilder
                .baseUrl(baseStreamUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_EVENT_STREAM_VALUE)
                .filter(logRequest())
                .filter(oauthFilter(oAuthSignatureUtils))
                .build();
    }

    public Flux<TweetDTO> filterTweets(String word) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(filterUrl)
                        .queryParam(TwitterUtils.QUERY_PARAM_FILTER_KEY, word)
                        .build())
                .retrieve()
                .bodyToFlux(TweetDTO.class)
                .take(30)                           // take at most 30 elements
                .take(Duration.ofSeconds(100));     // or until 100 second has passed
    }

    /**
     * Helper function to add oauth headers to webclient
     */
    private ExchangeFilterFunction oauthFilter(OAuthSignatureUtils oauthUtil) {
        return ExchangeFilterFunction.ofRequestProcessor(req -> {
            ClientRequest oauthReq = ClientRequest.from(req)
                    .headers(headers -> headers.add(HttpHeaders.AUTHORIZATION, oauthUtil.oAuth1Header(req)))
                    .build();
            return Mono.just(oauthReq);
        });
    }

    /**
     * Helper function to log requests.
     */
    private ExchangeFilterFunction logRequest() {
        return (clientRequest, next) -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers()
                    .forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return next.exchange(clientRequest);
        };
    }
}
