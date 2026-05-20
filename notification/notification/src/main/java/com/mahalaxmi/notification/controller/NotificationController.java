package com.mahalaxmi.notification.controller;



import com.mahalaxmi.notification.dto.SendOtpRequestDto;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Value("${twilio_sid}")
    String sid;

    @Value("${twilio_secret}")
    String secret;

    @PostMapping("/sendOTP")
    public void sendOTP(@RequestBody SendOtpRequestDto sendOtpRequestDto){
        String twilioNumber =  "+19125518750";
        System.out.println("Inside SendOtp");
        Twilio.init(sid,secret);

        Message.creator(new PhoneNumber(sendOtpRequestDto.getPhone()),
                new PhoneNumber(twilioNumber),
                "Verification Code for Mahalaxmi Enterprise is "+ sendOtpRequestDto.getCode()).create();
    }

}
