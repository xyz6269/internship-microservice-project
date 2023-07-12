package com.example.orderservice.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageOrder {

    private String orderNumber;
    private Double fullPrice;
    private String customerEmail;
    List<ItemResponse> items = new ArrayList<>();
}
