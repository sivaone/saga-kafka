package com.sivanagireddy.orders.controller;

import com.sivanagireddy.domain.Order;
import com.sivanagireddy.orders.service.OrderGeneratorService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreType;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

  private static AtomicLong id = new AtomicLong();

  private final KafkaTemplate<Long, Order> kafkaTemplate;
  private final StreamsBuilderFactoryBean streamsBuilderFactory;
  private final OrderGeneratorService orderGeneratorService;

  @PostMapping
  public Order create(@RequestBody Order order) {
    order.setId(id.incrementAndGet());
    kafkaTemplate.send("orders", order.getId(), order);
    log.info("Sent: {}", order);
    return order;
  }

  @PostMapping("/generate")
  @ResponseStatus(HttpStatus.CREATED)
  public void create() {
    orderGeneratorService.generate();
  }

  @GetMapping
  public List<Order> allOrders(){
    List<Order> orders = new ArrayList<>();

    ReadOnlyKeyValueStore<Long, Order> store = Objects.requireNonNull(
            streamsBuilderFactory.getKafkaStreams())
        .store(StoreQueryParameters.fromNameAndType(
            "orders", QueryableStoreTypes.keyValueStore()));
    KeyValueIterator<Long, Order> it = store.all();
    it.forEachRemaining(kv -> orders.add(kv.value));
    return orders;
  }
}
