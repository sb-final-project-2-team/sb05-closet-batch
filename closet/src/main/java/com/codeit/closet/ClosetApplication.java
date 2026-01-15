package com.codeit.closet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ClosetApplication {

  public static void main(String[] args) {
    SpringApplication.run(ClosetApplication.class, args);
  }

}
