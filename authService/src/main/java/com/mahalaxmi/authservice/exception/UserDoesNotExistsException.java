package com.mahalaxmi.authservice.exception;

public class UserDoesNotExistsException extends RuntimeException{
    public UserDoesNotExistsException(String msg){
        super(msg);
    }
}
