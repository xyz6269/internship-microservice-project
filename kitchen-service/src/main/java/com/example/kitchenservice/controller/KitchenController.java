package com.example.kitchenservice.controller;

import com.example.kitchenservice.entity.Order;
import com.example.kitchenservice.service.KitchenService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kitchen")
@AllArgsConstructor
public class KitchenController {

    private final KitchenService kitchenService;

    @GetMapping("/next")
    @PreAuthorize("hasRole('ROLE_CHEF')")
    public Order getnextOrder() {
        return kitchenService.getOrdersQueue();
    }
    @GetMapping("/ready")
    @PreAuthorize("hasRole('ROLE_CHEF')")
    public String orderReady() {
        return kitchenService.prepareOrder();
    }
}
