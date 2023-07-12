package com.example.menuservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection= "items")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Item {


    private String id;
    private String name;
    private Double price;

    public Item() {
        this.id = UUID.randomUUID().toString();
    }
}
