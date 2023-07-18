package com.example.demomicroservice;

import com.example.demomicroservice.service.AuthorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class AuthorServiceApplication {

  private final static Logger LOGGER = LoggerFactory.getLogger(AuthorServiceApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(AuthorServiceApplication.class, args);
    LOGGER.error("Information user euruka");
    LOGGER.error("Information user euruka");
    LOGGER.error("Information user euruka");
  }

}
