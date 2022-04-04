package com.sivanagireddy.stock;

import com.sivanagireddy.stock.domain.Product;
import com.sivanagireddy.stock.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.Random;
import java.util.stream.IntStream;

@SpringBootApplication
public class StockServiceApplication {

    @Autowired
    private ProductRepository productRepository;

    public static void main(String[] args) {
        SpringApplication.run(StockServiceApplication.class, args);
    }

    @PostConstruct
    public void generateData() {
        Random r = new Random();

        IntStream.range(0, 1000).forEach(i -> {
            int count = r.nextInt(1000);
            Product p = new Product(null, "Product" + i, count, 0);
            productRepository.save(p);
        });
    }
}
