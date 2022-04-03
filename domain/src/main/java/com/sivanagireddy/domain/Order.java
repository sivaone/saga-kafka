package com.sivanagireddy.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {

  private Long id;
  private Long customerId;
  private Long productId;
  private int productCount;
  private int price;
  private String status;
  private String source;
}
