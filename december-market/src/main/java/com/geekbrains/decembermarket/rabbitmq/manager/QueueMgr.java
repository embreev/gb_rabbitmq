package com.geekbrains.decembermarket.rabbitmq.manager;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class QueueMgr {
    private static final String Q_NEW = "new_order";
    private static final String Q_CONFIRMED = "confirmed_order";
    private static Channel channelIn;
    private static Channel channelOut;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        channelIn = connection.createChannel();
        channelOut = connection.createChannel();

        channelIn.exchangeDeclare(Q_NEW, "fanout");
        channelOut.exchangeDeclare(Q_CONFIRMED, "fanout");

        String queueNameIn = channelIn.queueDeclare().getQueue();
        System.out.println("My IN queue name: " + queueNameIn);
        channelIn.queueBind(queueNameIn, Q_NEW, "");

        System.out.println(" [*] Waiting for messages...");
        sendMessage("0:connected");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody());

            System.out.println(" [<] Received '" + message + "'");

            String orderId = message.split(":")[0];
            String orderStatus = message.split(":")[1];

            if (orderStatus.toLowerCase().equals("new")) {
                sendMessage(orderId + ":");
            }
        };

        channelIn.basicConsume(queueNameIn, true, deliverCallback, consumerTag -> {});
    }

    private static void sendMessage(String msg) throws IOException {
        if (msg.split(":").length == 1) {
            System.out.println(" [!] Enter message:");
            msg = msg + scanner.next();
        }
        channelOut.basicPublish(Q_CONFIRMED, "", null, msg.getBytes("UTF-8"));
        System.out.println(" [>] Sent '" + msg + "'");
    }
}
