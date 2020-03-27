package com.demo.client.actor;

import com.entity.Order;

public class MainTest {

    public static void main(String[] args) throws InterruptedException {
        SDK.init();
        Thread.sleep(3000l);
        SDK.createOrder(new Order.CreateOrder("1"));
    }
}
