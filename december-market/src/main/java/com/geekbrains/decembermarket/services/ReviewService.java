package com.geekbrains.decembermarket.services;

import com.geekbrains.decembermarket.entites.Review;
import com.geekbrains.decembermarket.entites.User;
import com.geekbrains.decembermarket.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    private ReviewRepository reviewRepository;

    @Autowired
    public void setReviewRepository(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    public boolean isUserCanWriteReview(Long productId, User user) {
        return reviewRepository.findByProductIdAndUser(productId, user) == null;
    }
}