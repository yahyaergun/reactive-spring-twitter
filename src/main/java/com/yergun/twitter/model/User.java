package com.yergun.twitter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class User {
    @Id
    private Long id;
    private String name;
    private String screenName;
    private Long createdAt;
    private Set<Message> messages;

    public User addMessage(Message message) {
        if (messages == null) {
            messages = new TreeSet<>(Comparator.comparing(Message::getCreatedAt));
        }

        messages.add(message);
        return this;
    }
}
