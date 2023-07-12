package com.example.orderservice.service;

import com.example.orderservice.config.MQConfig;
import com.example.orderservice.dto.ItemResponse;
import com.example.orderservice.dto.NotificationDTO;
import com.example.orderservice.dto.OrderDTO;
import com.example.orderservice.dto.MessageOrder;
import com.example.orderservice.entity.Item;
import com.example.orderservice.entity.Order;
import com.example.orderservice.repository.ItemRepository;
import com.example.orderservice.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.util.*;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final RabbitTemplate template;
    private final HttpServletRequest request;
    private final WebClient webClient = WebClient.create("http://localhost:8080");

    public void addItemToOrder(ItemResponse dto) {
        String customerEmail = getCurrentUserEmail();
        Order order;
        try {
            order = orderRepository.findOrderByCustomerEmail(customerEmail).orElseThrow(() -> new RuntimeException("no order placed yet by this customer"));
        } catch (Exception e) {
            order = new Order();
            order.setCustomerEmail(customerEmail);
            order.setOrderNumber(UUID.randomUUID().toString());
        }
        if(order.isSubmitted()){
            throw new RuntimeException("you already have an order in process, please wait");
        }
        Item itemToAdd = DtoMapper(dto);
        order.getItems().add(itemToAdd);
        order.setFullPrice(order.getItems().stream().mapToDouble(Item::getTotalPrice).sum());
        orderRepository.save(order);
    }

    public void submitOrder() {
        String customerEmail = getCurrentUserEmail();
        Order order = orderRepository.findOrderByCustomerEmail(customerEmail).orElseThrow(() -> new RuntimeException("no order placed yet by this customer"));
        if(order.isSubmitted()){
            throw new RuntimeException("you already have an order in process, please wait");
        }
        order.setSubmitted(true);
        List<ItemResponse> dtos = order.getItems()
                .stream()
                .map(this::itemToDto)
                .toList();
        OrderDTO dto = new OrderDTO(dtos);
        validateOrder(order,dto);
    }

    public void validateOrder(Order order , OrderDTO dto) {
       MessageOrder message = MessageMapper(dto,order);
       template.convertAndSend(MQConfig.EXCHANGE, MQConfig.ROUTING_KEY, message);
       sendNotification("your order has been submitted");
    }

    public void deleteOrder(String email) {
        Order order =  orderRepository.findOrderByCustomerEmail(email).orElseThrow(() -> new RuntimeException("no order placed yet by this customer"));
        orderRepository.delete(order);
    }

    private Item DtoMapper(ItemResponse dto) {
        Item item = Item.builder()
                .name(dto.getName())
                .itemPrice(dto.getPrice())
                .quantity(dto.getQuantity())
                .totalPrice(dto.getFullPrice())
                .build();
        itemRepository.save(item);
        return item;
    }

    private ItemResponse itemToDto(Item item) {
        ItemResponse dto = new ItemResponse();
        dto.setName(item.getName());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getItemPrice());
        return dto;
    }

    private MessageOrder MessageMapper(OrderDTO dto ,Order order) {
        MessageOrder messageOrder = MessageOrder.builder()
                .orderNumber(order.getOrderNumber())
                .customerEmail(order.getCustomerEmail())
                .fullPrice(order.getFullPrice())
                .items(dto.getItems())
                .build();

        return messageOrder;
    }

    @Transactional(readOnly = true)
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(authentication.getName());
        if ((authentication instanceof AnonymousAuthenticationToken)) {
         throw new RuntimeException("the jwt is dead or some shit lol");
        }
            return authentication.getName();
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    private String sendNotification(String context) {
        NotificationDTO dto = new NotificationDTO();
        dto.setContext(context);
        dto.setEmail(getCurrentUserEmail());
        webClient.post()
                .uri("/api/noti/new-noti")
                .header("Authorization", "Bearer " + getJwt())
                .body(Mono.just(dto), NotificationDTO.class)
                .retrieve()
                .bodyToMono(Void.class) // Assuming the response is of type String
                .block();
        return "notification transmitted";
    }

    private String getJwt() {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }


}
