package com.example.kitchenservice.messaging;


import com.example.kitchenservice.dto.MessageOrder;
import com.example.kitchenservice.service.KitchenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageListner {

    private final KitchenService kitchenService;
    @RabbitListener(queues = MQConfig.QUEUE)
    public void listener(MessageOrder message) {
        kitchenService.createOrder(message);
    }

}
