package com.demo.client.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.entity.Order;

public class SDK extends AbstractBehavior{

    private static ActorSystem<Void> actorSystem;
    private static ActorContext actorContext;
    private static ActorRef clientOrderRef;
    private static ActorSelection serverOrderDestination;


    public static void init () {
        actorSystem = ActorSystem.create(SDK.create(),"sdk");
    }

    private static Behavior<Void> create() {
        return Behaviors.setup(SDK::new);
    }


    public static void createOrder(Order.CreateOrder order) {
        clientOrderRef.tell(order,null);
    }

    private SDK(akka.actor.typed.javadsl.ActorContext context) {
        super(context);
        //todo 查找远端actor 后续需要封装为统一方法
        actorContext = context.classicActorContext();
        serverOrderDestination =  actorContext.actorSelection("akka.tcp://sdk@127.0.0.1:8001/user/serverOrderActor");
        clientOrderRef = actorContext.actorOf(Props.create(ClientOrderActor.class, serverOrderDestination,context.getSelf()),"client");

    }

    @Override
    public Receive createReceive() {
        return newReceiveBuilder().build();
    }
}
