package com.demo.server.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.entity.Order;

public class SDK extends AbstractBehavior {

    public static void init () {
        ActorSystem<Void> actorSystem = ActorSystem.create(SDK.create(),"sdk");
    }

    private SDK(ActorContext context) {
        super(context);

    }

    private static Behavior<Void> create() {
        return Behaviors.setup(ctx -> {
            ActorRef<Order> actorRef = ctx.spawn(ServerOrderActor.create(),"serverOrderActor");
            return Behaviors.empty();
        });
    }


    @Override
    public Receive createReceive() {
        return newReceiveBuilder().build();
    }
}


