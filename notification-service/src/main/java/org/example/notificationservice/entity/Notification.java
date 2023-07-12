package org.example.notificationservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection= "notifications")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    private String id;
    private String context;
    private String deliveryGuyNumber;
    private String email;
    private Double price;

    public Notification() {
        this.id = UUID.randomUUID().toString();
    }
}
