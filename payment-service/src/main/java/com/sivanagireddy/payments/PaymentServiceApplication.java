package com.sivanagireddy.payments;

import com.sivanagireddy.payments.domain.Customer;
import com.sivanagireddy.payments.repository.CustomerRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

import javax.annotation.PostConstruct;
import java.util.Random;
import java.util.stream.IntStream;

@SpringBootApplication
@EnableKafka
public class PaymentServiceApplication {

    @Autowired
    private CustomerRepository customerRepository;

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }

    @PostConstruct
    public void generateData() {
        Random random = new Random();
        Faker faker = new Faker();
        IntStream.range(0, 100).forEach(i -> {
            int count = random.nextInt(1000);
            Customer c = new Customer(null, faker.name().fullName(), count, 0);
            customerRepository.save(c);
        });
    }

}
