package com.example.kitchenservice.dto;

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
    List<ItemDTO> items = new ArrayList<>();
}
