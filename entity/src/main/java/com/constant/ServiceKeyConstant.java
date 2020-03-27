package com.constant;

import akka.actor.typed.receptionist.ServiceKey;
import com.entity.Order;

public class ServiceKeyConstant {

    public static final ServiceKey<Order> orderServiceKey =
            ServiceKey.create(Order.class, "ServerOrderKey");
}
