package com.sivanagireddy.payments.service;

import com.sivanagireddy.domain.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderListener {

    private final OrderManagementService orderManagementService;

    @KafkaListener(id = "orders", topics = "orders", groupId = "payment")
    public void onEvent(Order order) {
      log.info("Received: {}", order);

      if (order.getStatus().equals("NEW")) {
          orderManagementService.reserve(order);
      } else {
          orderManagementService.confirm(order);
      }
    }
}
