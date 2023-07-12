package com.example.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderNumber;
    private Double fullPrice;
    @Column(unique = true)
    private String customerEmail;
    @OneToMany(cascade = CascadeType.ALL)
    List<Item> items = new ArrayList<>();
    private boolean isSubmitted;
}
