package com.geekbrains.decembermarket.controllers;

import com.geekbrains.decembermarket.entites.Category;
import com.geekbrains.decembermarket.entites.Product;
import com.geekbrains.decembermarket.entites.Review;
import com.geekbrains.decembermarket.entites.User;
import com.geekbrains.decembermarket.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    private ProductService productService;
    private CategoryService categoryService;
    private ReviewService reviewService;
    private OrderItemService orderItemService;
    private UserService userService;

    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService, ReviewService reviewService, OrderItemService orderItemService, UserService userService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.reviewService = reviewService;
        this.orderItemService = orderItemService;
        this.userService = userService;
    }

    @GetMapping("/edit/{id}")
    public String editProductForm(Model model, @PathVariable Long id) {
        Product product = productService.findById(id);
        List<Category> categories = categoryService.getAll();
        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        return "edit_product";
    }

    @PostMapping("/edit")
    public String saveProduct(@ModelAttribute(name = "product") Product product) {
        productService.save(product);
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String showProduct(Model model, Principal principal, @PathVariable Long id) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
//        product.getReviews(); // todo убрать это отсюда
        if (principal != null) {
            User user = userService.findByPhone(principal.getName());
            if (orderItemService.isUserCanWriteReview(id, user) && reviewService.isUserCanWriteReview(id, user)) {
                model.addAttribute("allowed", true);
            }
        }
        return "product_page";
    }

    @PostMapping("/{id}/review")
    public String saveComment(@ModelAttribute Review review, @PathVariable Long id, Principal principal) {
        review.setProduct(productService.findById(id));
        review.setUser(userService.findByPhone(principal.getName()));
        reviewService.save(review);
        return "redirect:/products/" + id;
    }
}
