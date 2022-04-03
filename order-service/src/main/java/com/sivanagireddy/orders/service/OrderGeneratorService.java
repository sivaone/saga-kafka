package com.sivanagireddy.orders.service;

import com.sivanagireddy.domain.Order;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderGeneratorService {
  private static Random RAND = new Random();
  private AtomicLong id = new AtomicLong();

  private final KafkaTemplate<Long, Order> kafkaTemplate;

  @Async
  public void generate() {
    for (int i = 0; i < 100; i++) {
      int x = RAND.nextInt(5) + 1;
      final Order o = Order.builder()
          .id(id.incrementAndGet())
          .customerId(RAND.nextLong(1000))
          .productId(RAND.nextLong(1000))
          .status("NEW")
          .price(100 * x)
          .productCount(x)
          .build();
      kafkaTemplate.send("orders", o.getId(), o);
    }
  }
}
