package com.demo.client.actor;

import akka.actor.ActorSelection;
import akka.actor.typed.ActorRef;
import akka.actor.typed.receptionist.Receptionist;
import akka.dispatch.OnComplete;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;
import akka.japi.pf.ReceiveBuilder;
import akka.pattern.Patterns;
import akka.persistence.AbstractPersistentActorWithAtLeastOnceDelivery;
import akka.util.Timeout;
import com.entity.Order;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

    public class ClientOrderActor extends AbstractPersistentActorWithAtLeastOnceDelivery {
        LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private ActorSelection actorSelection;

      private ActorRef sdkActorRef;

    public String persistenceId() {
        return "order";
    }

    public ClientOrderActor(ActorSelection actorSelection,ActorRef actorRef) {
        this.sdkActorRef = actorRef;
        this.actorSelection = actorSelection;
    }

    //处理创建订单 更新订单操作
    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(Order.CreateOrder.class, this::createOrder)
                .match(Order.UpdateOrder.class, this::updateOrder)
                .match(Order.QueryOrder.class, this::queryOrder)
                .match(Order.ConfirmedOrder.class, this::confirmedOrder)
                .build();
    }

        private void confirmedOrder(Order.ConfirmedOrder confirmedOrder) {
            persist(new Order.ConfirmedOrder(confirmedOrder.getDeliveryId(),confirmedOrder.getMessage()), evt -> {
                log.info("订单处理成功：deliveryId：{}", evt.getDeliveryId());
                confirmDelivery(evt.getDeliveryId());
            });
        }

        private void queryOrder(Order.QueryOrder queryOrder) {
        Future<Object> future = Patterns.ask(actorSelection,queryOrder, new Timeout(FiniteDuration.create(3,"seconds")));
        future.onComplete(new OnComplete<Object>() {
                              public void onComplete(Throwable throwable, Object o) throws Throwable {
                                  if (throwable != null) {
                                      log.error("查询订单异常:",throwable.getLocalizedMessage());
                                  } else {
                                      getSender().tell(o,getSelf());
                                  }
                              }
                          }, getContext().dispatcher());

    }


    private void createOrder(Order.CreateOrder createOrder) {
        log.info("开始处理新订单-----> 订单id:{}",createOrder.getOrderId());
        persist(createOrder, order -> deliver(actorSelection, deliveryId -> new Order.SendCreateOrder(deliveryId,createOrder)));
    }

    private  void updateOrder(Order.UpdateOrder updateOrder) {
        persist(updateOrder, order -> deliver(actorSelection, deliveryId -> new Order.SendUpdateOrder(deliveryId,updateOrder)));
    }

    public Receive createReceiveRecover() {
        return ReceiveBuilder.create().match(Order.class, this::onReceiveCommand).build();
    }

    private void onReceiveCommand(Order order) {
        if(order instanceof Order.CreateOrder) {
            deliver(actorSelection,deliveryId -> new Order.SendCreateOrder(deliveryId,(Order.CreateOrder)order));
        } else if (order instanceof Order.UpdateOrder) {
            deliver(actorSelection,deliveryId -> new Order.SendUpdateOrder(deliveryId,(Order.UpdateOrder)order));
        }else if(order instanceof Order.ConfirmedOrder) {
            log.info("订单恢复成功：deliveryId：{}",((Order.ConfirmedOrder) order).getDeliveryId());
            confirmDelivery(((Order.ConfirmedOrder)order).getDeliveryId());
        }
    }


}
