package com.geekbrains.decembermarket.services;

import com.geekbrains.decembermarket.entites.User;
import com.geekbrains.decembermarket.repositories.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {
    private OrderItemRepository orderItemRepository;

    @Autowired
    public void setOrderItemRepository(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    public boolean isUserCanWriteReview(Long productId, User user) {
        return orderItemRepository.findByProductIdAndUser(productId, user) != null;
    }
}