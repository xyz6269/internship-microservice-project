package com.example.kitchenservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double price;
    @Column(unique = true)
    private String customerEmail;
    @OneToMany(fetch = FetchType.EAGER ,cascade = CascadeType.MERGE)
    List<Item> items = new ArrayList<>();
}
