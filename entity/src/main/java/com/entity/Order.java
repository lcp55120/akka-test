package com.entity;

import akka.actor.ActorRef;

import java.io.Serializable;

public interface Order extends Serializable {

    abstract class OrderCommand implements Order {

        private ActorRef actorRef;
        private String orderId;
        public OrderCommand (String orderId) {
            this.orderId = orderId;
        }

        public String getOrderId() {
            return orderId;
        }
    }

    class CreateOrder extends OrderCommand {
        public CreateOrder (String orderId) {
            super(orderId);
        }
    }

    class UpdateOrder extends OrderCommand {
        public UpdateOrder (String orderId) {
            super(orderId);
        }
    }

    class QueryOrder extends OrderCommand{
        public QueryOrder (String orderId) {
            super(orderId);
        }
    }

    abstract class Sender implements Order {
        private Long deliveryId;
        public Sender (Long deliveryId) {
            this.deliveryId = deliveryId;
        }

        public Long getDeliveryId() {
            return deliveryId;
        }
    }

    class SendCreateOrder extends Sender {
        private CreateOrder createOrder;
        public SendCreateOrder(Long deliveryId, CreateOrder createOrder){
            super(deliveryId);
            this.createOrder = createOrder;
        }

        public CreateOrder getCreateOrder() {
            return createOrder;
        }
    }

    class SendUpdateOrder extends Sender{
        private UpdateOrder updateOrder;
        public SendUpdateOrder(Long deliveryId, UpdateOrder updateOrder){
            super(deliveryId);
            this.updateOrder = updateOrder;
        }

        public UpdateOrder getUpdateOrder() {
            return updateOrder;
        }
    }

    class ConfirmedOrder extends Sender {
        private String message;
        public ConfirmedOrder(Long deliveryId,String message) {
            super(deliveryId);
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
