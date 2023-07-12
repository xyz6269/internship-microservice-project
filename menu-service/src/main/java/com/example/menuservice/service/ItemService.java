package com.example.menuservice.service;


import com.example.menuservice.dto.ItemDTO;
import com.example.menuservice.dto.ItemResponse;
import com.example.menuservice.entity.Item;

import com.example.menuservice.repository.ItemRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.awt.*;
import java.util.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository menuItemRepository;
    private final HttpServletRequest request;
    private final WebClient webClient = WebClient.create("http://localhost:8080");
    public void addItem(ItemDTO dto) {
        Item newItem = new Item();
        newItem.setName(dto.getName());
        newItem.setPrice(dto.getPrice());
        menuItemRepository.save(newItem);
    }
    public void removeItem(String id) {
        Item itemToRemove = menuItemRepository.findById(id).orElseThrow(() -> new RuntimeException("this item isn't available"));
        menuItemRepository.delete(itemToRemove);
    }

    public List<Item> getFullMenu() {
        List<Item> menu = menuItemRepository.findAll();
        menu.sort(Comparator.comparing(Item::getPrice));
        return menu;
    }

    public String AddItem(String id , Integer quantity){
        Item item = menuItemRepository.findById(id).orElseThrow(() -> new RuntimeException("this item isn't available"));
        ItemResponse response = new ItemResponse();
        response.setQuantity(quantity);
        response.setPrice(item.getPrice());
        response.setName(item.getName());
        response.setFullPrice(item.getPrice()*quantity);
        return webClient.post()
                .uri("/api/order/add-item-order")
                .header("Authorization", "Bearer " + getJwt())
                .body(Mono.just(response), ItemResponse.class)
                .retrieve()
                .bodyToMono(String.class) // Assuming the response is of type String
                .block();
    }

    private String getJwt() {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }




}
