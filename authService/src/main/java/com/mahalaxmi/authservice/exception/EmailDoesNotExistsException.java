package com.mahalaxmi.authservice.exception;

public class EmailDoesNotExistsException extends RuntimeException{
    public EmailDoesNotExistsException(String msg){
        super(msg);
    }
}
