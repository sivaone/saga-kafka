package com.sivanagireddy.stock.service;

import com.sivanagireddy.domain.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderListener {

    private final OrderManagementService orderManagementService;

    @KafkaListener(id = "orders", topics = "orders", groupId = "stock")
    public void onEvent(final Order order) {
        log.info("Received: {}", order);
        if (order.getStatus().equals("NEW")) {
            orderManagementService.reserve(order);
        } else {
            orderManagementService.confirm(order);
        }
    }
}
