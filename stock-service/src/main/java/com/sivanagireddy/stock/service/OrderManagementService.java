package com.sivanagireddy.stock.service;

import com.sivanagireddy.domain.Order;
import com.sivanagireddy.stock.domain.Product;
import com.sivanagireddy.stock.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderManagementService {

    private final ProductRepository productRepository;
    private final KafkaTemplate<Long, Order> kafkaTemplate;

    public void reserve(Order order) {
        Product product = productRepository.findById(order.getProductId()).orElseThrow();
        log.info("Found: {}", product);
        if (order.getStatus().equals("NEW")) {
            if (order.getProductCount() < product.getAvailableItems()) {
                product.setReservedItems(product.getReservedItems() + order.getProductCount());
                product.setAvailableItems(product.getAvailableItems() - order.getProductCount());
                order.setStatus("ACCEPT");
                productRepository.save(product);
            } else {
                order.setStatus("REJECT");
            }
            kafkaTemplate.send("stock-orders", order.getId(), order);
            log.info("Sent: {}", order);
        }
    }

    public void confirm(Order order) {
        Product product = productRepository.findById(order.getProductId()).orElseThrow();
        log.info("Found: {}", product);
        if (order.getStatus().equals("CONFIRMED")) {
            product.setReservedItems(product.getReservedItems() - order.getProductCount());
            productRepository.save(product);
        } else if (order.getStatus().equals("ROLLBACK") && !order.getSource().equals("stock")) {
            product.setReservedItems(product.getReservedItems() - order.getProductCount());
            product.setAvailableItems(product.getAvailableItems() + order.getProductCount());
            productRepository.save(product);
        }
    }
}
