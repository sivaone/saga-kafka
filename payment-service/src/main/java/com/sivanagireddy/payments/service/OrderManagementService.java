package com.sivanagireddy.payments.service;

import com.sivanagireddy.domain.Order;
import com.sivanagireddy.payments.domain.Customer;
import com.sivanagireddy.payments.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderManagementService {

    private final CustomerRepository customerRepository;
    private final KafkaTemplate<Long, Order> kafkaTemplate;

    public void reserve(final Order order) {
        final Customer customer = customerRepository.findById(order.getCustomerId()).orElseThrow();
        log.info("Customer found: {}", customer);

        if(order.getPrice() < customer.getAmountAvailable()) {
            order.setStatus("ACCEPT");
            customer.setAmountReserved(customer.getAmountReserved() + order.getPrice());
            customer.setAmountAvailable(customer.getAmountAvailable() - order.getPrice());

        } else {
            order.setStatus("REJECT");
        }

        order.setSource("payment");
        customerRepository.save(customer);
        kafkaTemplate.send("payment-orders", order.getId(), order);
        log.info("Sent: {}", order);
    }

    public void confirm(final Order order) {
        final Customer customer = customerRepository.findById(order.getCustomerId()).orElseThrow();
        log.info("Customer found: {}", customer);

        if (order.getStatus().equals("CONFIRMED")) {
            customer.setAmountReserved(customer.getAmountReserved() - order.getPrice());
            customerRepository.save(customer);
        } else if (order.getStatus().equals("ROLLBACK") && !order.getSource().equals("payment")) {
            customer.setAmountReserved(customer.getAmountReserved() - order.getPrice());
            customer.setAmountAvailable(customer.getAmountAvailable() + order.getPrice());
            customerRepository.save(customer);
        }
    }
}
