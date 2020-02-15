package com.geekbrains.decembermarket.repositories;

import com.geekbrains.decembermarket.entites.OrderItem;
import com.geekbrains.decembermarket.entites.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("select o from OrderItem o where o.product.id = ?1 and o.order.user = ?2")
    OrderItem findByProductIdAndUser(Long productId, User user);
}
