package com.learning.orderservice.function;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class OrderNotification {

    @Bean
    public Consumer<String> orderPlacedNotification(){
        return(msg)->{
            System.out.println("Inside acknowledgement " + msg);
        };

    }
}
