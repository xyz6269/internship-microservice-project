package com.example.orderservice.dto;


import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {

    List<ItemResponse> items = new ArrayList<>();
}
