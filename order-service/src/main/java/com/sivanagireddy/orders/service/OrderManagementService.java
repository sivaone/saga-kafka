package com.sivanagireddy.orders.service;

import com.sivanagireddy.domain.Order;
import org.springframework.stereotype.Service;

@Service
public class OrderManagementService {

  public Order confirm(Order orderPayment, Order orderStock) {
    Order o = Order.builder()
        .id(orderPayment.getId())
        .customerId(orderPayment.getCustomerId())
        .productId(orderPayment.getProductId())
        .productCount(orderPayment.getProductCount())
        .price(orderPayment.getPrice())
        .build();
    if (orderPayment.getStatus().equals("ACCEPT") &&
        orderStock.getStatus().equals("ACCEPT")) {
      o.setStatus("CONFIRMED");
    } else if (orderPayment.getStatus().equals("REJECT") &&
        orderStock.getStatus().equals("REJECT")) {
      o.setStatus("REJECTED");
    } else if (orderPayment.getStatus().equals("REJECT") ||
        orderStock.getStatus().equals("REJECT")) {
      String source = orderPayment.getStatus().equals("REJECT")
          ? "PAYMENT" : "STOCK";
      o.setStatus("ROLLBACK");
      o.setSource(source);
    }
    return o;
  }
}
