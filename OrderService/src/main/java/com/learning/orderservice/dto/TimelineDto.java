package com.learning.orderservice.dto;


import lombok.Data;

@Data
public class TimelineDto {
    private String label;
    private String date;
    private boolean done;
}
