package com.demo.server.actor;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import com.constant.ServiceKeyConstant;
import com.entity.Order;

public class ServerOrderActor {

    private ActorContext<Order> actorContext;

    private ServerOrderActor(ActorContext<Order> actorContext) {
        this.actorContext = actorContext;
    }

    public static Behavior<Order> create() {
        return Behaviors.setup(context -> {

            context.getSystem().receptionist().tell(Receptionist.register(ServiceKeyConstant.orderServiceKey, context.getSelf()));
            return new ServerOrderActor(context).serverOrder();
        });
    }


    public Behavior<Order> serverOrder() {
        return Behaviors.receive(Order.class)
                .onMessage(Order.SendCreateOrder.class, senderCreateOrder -> handleCreateOrder(senderCreateOrder))
                .onMessage(Order.SendUpdateOrder.class, updateOrder -> handleUpdateOrder(updateOrder))
                .onMessage(Order.QueryOrder.class, queryOrder -> handleQueryOrder(queryOrder))
                .build();
    }

    private Behavior<Order> handleQueryOrder(Order.QueryOrder queryOrder) {
        actorContext.getSystem().log().info("开始查询订单----订单号：",queryOrder.getOrderId());
        actorContext.classicActorContext().sender().tell("订单查询成功",actorContext.classicActorContext().sender());
        return Behaviors.same();
    }

    private Behavior<Order> handleUpdateOrder(Order.SendUpdateOrder updateOrder) {
        actorContext.getSystem().log().info("开始处理更新订单----订单号：{},deliveryId:{}",updateOrder.getUpdateOrder().getOrderId(),updateOrder.getDeliveryId());
        //todo 通知消费者同步订单信息
        actorContext.classicActorContext().sender().tell(new Order.ConfirmedOrder(updateOrder.getDeliveryId(),"订单更新成功"),actorContext.classicActorContext().self());
        return Behaviors.same();
    }

    private Behavior<Order> handleCreateOrder(Order.SendCreateOrder order) {
        actorContext.getSystem().log().info("开始处理新订单----订单号：{},deliveryId:{}",order.getCreateOrder().getOrderId(),order.getDeliveryId());
        //todo 通知消费者同步订单信息
        actorContext.classicActorContext().sender().tell(new Order.ConfirmedOrder(order.getDeliveryId(),"订单创建成功"),actorContext.classicActorContext().self());
        return Behaviors.same();
    }


}
