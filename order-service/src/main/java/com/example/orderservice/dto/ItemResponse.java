package com.example.orderservice.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponse {

    private String name;
    private Double price;
    private Double fullPrice;
    private Integer quantity;
}