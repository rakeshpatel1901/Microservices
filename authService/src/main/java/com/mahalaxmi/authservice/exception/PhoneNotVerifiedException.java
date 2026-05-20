package com.mahalaxmi.authservice.exception;

public class PhoneNotVerifiedException extends RuntimeException{
    public PhoneNotVerifiedException(String msg){
        super(msg);
    }
}
