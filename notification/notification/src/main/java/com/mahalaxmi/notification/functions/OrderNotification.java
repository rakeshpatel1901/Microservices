package com.mahalaxmi.notification.functions;


import com.mahalaxmi.notification.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class OrderNotification {

    @Autowired
    NotificationService notificationService;

    @Bean
    public Function<String, String> orderPlacedNotification(){

        return(mail) -> {

            notificationService.sendMail(
                    mail,
                    "officialrbdtalks@gmail.com",
                    "MahaLaxmi Registration Successful",
                    """
                           <h2>Order Placed</h2>
                           <p>Order placed Successfully</p>
                           <br>
                           <p>Regards,<br><b>Team MahaLaxmi</b></p>
                          """
            );
                return "Order Notification response "+mail;

        };

    }

}
