package com.geekbrains.decembermarket.repositories;

import com.geekbrains.decembermarket.entites.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}