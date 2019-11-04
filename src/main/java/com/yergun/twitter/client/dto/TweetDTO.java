package com.yergun.twitter.client.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TweetDTO {
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "EEE MMM d HH:mm:ss Z yyyy")
    private ZonedDateTime createdAt;
    private String text;
    private UserDTO user;
}
