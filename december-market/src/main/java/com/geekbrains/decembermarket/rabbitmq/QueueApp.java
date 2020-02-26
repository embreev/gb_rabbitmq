package com.geekbrains.decembermarket.rabbitmq;

import com.geekbrains.decembermarket.services.OrderService;
import com.geekbrains.decembermarket.utils.OrderStatus;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class QueueApp {
    private static final String Q_CONFIRMED = "confirmed_order";
    private static final String Q_NEW = "new_order";

    private Channel channelIn;
    private Channel channelOut;

    private String queueNameIn;
    private String queueNameOut;

    private String msg;

    private OrderService orderService;

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostConstruct
    public void init() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        channelIn = connection.createChannel();
        channelOut = connection.createChannel();

        channelIn.exchangeDeclare(Q_CONFIRMED, "fanout");
        channelOut.exchangeDeclare(Q_NEW, "fanout");

        queueNameIn = channelIn.queueDeclare().getQueue();
        System.out.println("My IN queue name: " + queueNameIn);
        channelIn.queueBind(queueNameIn, Q_CONFIRMED, "");

        System.out.println(" [*] Waiting for messages...");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(message);

            String orderId = message.split(":")[0];
            String orderStatus = message.split(":")[1];

            if (orderStatus.toLowerCase().equals("ok")) {
                orderService.changeStatus(Long.parseLong(orderId), OrderStatus.Confirmed.name());
                System.out.println("Order " + orderId + " status: changed to " + orderStatus);
            }
        };

        channelIn.basicConsume(queueNameIn, true, deliverCallback, consumerTag -> {});

    }

    public void sendMessageToManager(Long order_id, String status) throws IOException {
        msg = order_id + ":" + status;
        System.out.println(msg);
        channelOut.basicPublish(Q_NEW, "", null, msg.getBytes("UTF-8"));
    }
}
