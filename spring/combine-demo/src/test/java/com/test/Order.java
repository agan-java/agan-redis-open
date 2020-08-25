package com.test;

import lombok.Data;

@Data
public class Order {
    private Integer userId;
    private String orderNo;
    private String payNo;
}
