package com.geekbrains.decembermarket.services;

import com.geekbrains.decembermarket.entites.Order;
import com.geekbrains.decembermarket.repositories.OrderRepository;
import com.geekbrains.decembermarket.utils.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private OrderRepository orderRepository;

    @Autowired
    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public void changeStatus(Long id, String status) {
        System.out.println("Status before: " + orderRepository.findById(id).get().getStatus());
        Order order = orderRepository.findById(id).get();
        order.setStatus(status);
        this.save(order);
        System.out.println("Status after: " + orderRepository.findById(id).get().getStatus());
    }
}
