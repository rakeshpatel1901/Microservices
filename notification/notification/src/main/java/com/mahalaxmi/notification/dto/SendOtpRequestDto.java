package com.mahalaxmi.notification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendOtpRequestDto {
    String phone;
    String code;
}
