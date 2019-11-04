package com.yergun.twitter.service;

import com.yergun.twitter.client.TwitterClient;
import com.yergun.twitter.client.dto.TweetDTO;
import com.yergun.twitter.client.dto.UserDTO;
import com.yergun.twitter.util.TwitterUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import com.yergun.twitter.model.Message;
import com.yergun.twitter.model.User;
import com.yergun.twitter.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Log4j2
public class TwitterService {

    private final TwitterClient twitterClient;
    private final UserRepository userRepository;

    public Flux<User> filterTweets(String trackValue) {
        return twitterClient
                .filterTweets(trackValue)
                .flatMap(this::transformAndSave)
                .log();
    }

    public Flux<User> findAllUsersSortedByCreationDate() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "createdAt"));
    }

    /**
     * Transform tweetDto to message,
     * Add message to user if there is one on mongo, otherwise transform userDto to user and add the message
     * Save/update record in mongo
     * All via reactive streams
     */
    private Mono<User> transformAndSave(TweetDTO tweet) {
        Long userId = tweet.getUser().getId();
        Message message = new Message();
        BeanUtils.copyProperties(tweet, message);
        message.setCreatedAt(TwitterUtils.convertZonedDateTimeToEpoch(tweet.getCreatedAt()));

        return userRepository.findById(userId)
                .switchIfEmpty(mapUserDtoToUser(tweet.getUser()))
                .flatMap(user -> userRepository.save(user.addMessage(message)));
    }

    private Mono<User> mapUserDtoToUser(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setCreatedAt(TwitterUtils.convertZonedDateTimeToEpoch(userDTO.getCreatedAt()));
        return Mono.just(user);
    }
}
