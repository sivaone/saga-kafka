package com.sivanagireddy.stock.repository;

import com.sivanagireddy.stock.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
