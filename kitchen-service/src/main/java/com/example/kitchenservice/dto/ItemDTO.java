package com.example.kitchenservice.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDTO {

    private String name;
    private Integer quantity;
    private Double price;
}