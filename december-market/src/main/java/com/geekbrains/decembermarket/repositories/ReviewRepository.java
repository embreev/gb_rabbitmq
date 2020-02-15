package com.geekbrains.decembermarket.repositories;

import com.geekbrains.decembermarket.entites.OrderItem;
import com.geekbrains.decembermarket.entites.Review;
import com.geekbrains.decembermarket.entites.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("select r from Review r where r.product.id = ?1 and r.user = ?2")
    Review findByProductIdAndUser(Long productId, User user);
}
