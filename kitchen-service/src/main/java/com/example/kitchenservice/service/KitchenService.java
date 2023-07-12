package com.example.kitchenservice.service;


import com.example.kitchenservice.dto.ItemDTO;
import com.example.kitchenservice.dto.MessageOrder;
import com.example.kitchenservice.dto.NotificationDTO;
import com.example.kitchenservice.entity.Item;
import com.example.kitchenservice.entity.Order;
import com.example.kitchenservice.repository.ItemRepository;
import com.example.kitchenservice.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class KitchenService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final HttpServletRequest request;
    private final WebClient webClient = WebClient.create("http://localhost:8080");

    public void createOrder(MessageOrder message) {
        log.info(message.getFullPrice().toString());
        log.info(message.getItems().toString());
        Order order = new Order();
        List<Item> items = message.getItems()
                .stream()
                .map(this::DtoMapper)
                .toList();
        order.setCustomerEmail(message.getCustomerEmail());
        order.setItems(items);
        order.setPrice(message.getFullPrice());
        log.info(order.getItems().toString());
        orderRepository.save(order);

    }

    private Item DtoMapper(ItemDTO dto) {
        Item item = new Item();
        item.setName(dto.getName());
        item.setQuantity(dto.getQuantity());
        itemRepository.save(item);
        return item;
    }

    public Order getOrdersQueue() {
        List<Order> queue = new ArrayList<>();
        queue = orderRepository.findAll();
        log.info(queue.toString());
        Collections.sort(queue, Comparator.comparing(Order::getId));
        log.info(queue.toString());
        return queue.get(0);
    }

    public String declineOrder() {
        Order ordertoprepare = getOrdersQueue();
        List<Item> items = getOrdersQueue().getItems();
        List<Long> itemsId = new ArrayList<>();
        items.forEach(item -> itemsId.add(item.getId()));
        log.info(itemsId.toString());
        orderRepository.delete(ordertoprepare);
        for (Long id: itemsId ) {
            itemRepository.deleteById(id);
        }
        sendNotification("apologies this order has been declined by the system", null,ordertoprepare.getCustomerEmail());
        return "order declined by the system";
    }

    public String prepareOrder() {
        Order ordertoprepare = getOrdersQueue();
        List<Item> items = getOrdersQueue().getItems();
        List<Long> itemsId = new ArrayList<>();
        items.forEach(item -> itemsId.add(item.getId()));
        log.info(itemsId.toString());
        orderRepository.delete(ordertoprepare);
        for (Long id: itemsId ) {
            itemRepository.deleteById(id);
        }
        webClient.delete()
                .uri("/api/order/delete-order/{email}", ordertoprepare.getCustomerEmail())
                .header("Authorization", "Bearer " + getJwt())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        sendNotification("your order is ready it was handed to a delivery guy make sure to contact him and provide you're location",
                         getDriverNumber(),
                         ordertoprepare.getCustomerEmail()
        );
        log.info(getDriverNumber());
        log.info(ordertoprepare.getPrice().toString());
        Double price = ordertoprepare.getPrice();
        sendNotificationToDriver("you have a an order to deliver", price);
        driverDelivery();
        return "order ready";
    }

    private String getJwt() {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private String sendNotification(String context, String phoneNumber,String email) {
        NotificationDTO dto = new NotificationDTO();
        dto.setContext(context);
        dto.setDriverNumber(phoneNumber);
        dto.setEmail(email);
        webClient.post()
                .uri("/api/noti/new-noti")
                .header("Authorization", "Bearer " + getJwt())
                .body(Mono.just(dto), NotificationDTO.class)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        return "notification transmitted";
    }

    private String sendNotificationToDriver(String context,Double price) {
        NotificationDTO dto = new NotificationDTO();
        dto.setContext(context);
        dto.setEmail(getDriverEmail());
        dto.setPrice(price);
        log.info(dto.getPrice().toString());
        webClient.post()
                .uri("/api/noti/new-noti")
                .header("Authorization", "Bearer " + getJwt())
                .body(Mono.just(dto), NotificationDTO.class)
                .retrieve()
                .bodyToMono(Void.class) // Assuming the response is of type String
                .block();
        return "notification transmitted";
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

    private String getDriverNumber(){
        return webClient.get()
                .uri("/api/delivery/get-driver")
                .header("Authorization", "Bearer " + getJwt())
                .accept(MediaType.TEXT_PLAIN)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private String getDriverEmail(){
        return webClient.get()
                .uri("/api/delivery/get-driver-email")
                .header("Authorization", "Bearer " + getJwt())
                .accept(MediaType.TEXT_PLAIN)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private void driverDelivery() {
        webClient.post()
                .uri("/api/delivery/deliver/{email}",getDriverEmail())
                .header("Authorization", "Bearer " + getJwt())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

}

