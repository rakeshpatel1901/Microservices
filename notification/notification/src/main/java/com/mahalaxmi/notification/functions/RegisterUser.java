package com.mahalaxmi.notification.functions;


import com.mahalaxmi.notification.services.NotificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class RegisterUser {


    NotificationService notificationService;

    public RegisterUser(NotificationService notificationService){
        this.notificationService = notificationService;
    }

    @Bean
    public Consumer<String> registerNotification(){
        return mail -> {
            System.out.println("Received message: " + mail);

            notificationService.sendMail(
                    mail,
                    "officialrbdtalks@gmail.com",
                    "MahaLaxmi Registration Successful",
                     """
                            <h2>Registration Successful</h2>
                            <p>You are one step closer to the best deals in the market.</p>
                            <p>Get your items at the most affordable prices. Let's Set Shop!!</p>
                            <br>
                            <p>Regards,<br><b>Team MahaLaxmi</b></p>
                           """
            );
        };
    }

}
