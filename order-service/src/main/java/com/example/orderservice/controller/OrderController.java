package com.example.orderservice.controller;

import com.example.orderservice.dto.ItemResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final HttpServletRequest request;

    @PostMapping("/add-item-order")
    public String addToOrder(@RequestBody ItemResponse dto){
        orderService.addItemToOrder(dto);
        return "Item added to order successfully";
    }
    @PostMapping("/submit-order")
    public String placeOrder(){
        orderService.submitOrder();
        return "order placed successfully";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/get-orders")
    public List<Order> getALlOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/user")
    public String getUserEmail() {
        return orderService.getCurrentUserEmail();
    }

    @GetMapping("/jwt")
    public String getJwt() {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    @DeleteMapping("/delete-order/{email}")
    public void removeOrder(@PathVariable("email") String email) {
       orderService.deleteOrder(email);
    }


}

//https://www.licence-professionnelle-maroc.com/?m=1