package com.mahalaxmi.notification.services;

public interface NotificationService {
    public void sendMail(String to,String from, String subject, String body);
}
