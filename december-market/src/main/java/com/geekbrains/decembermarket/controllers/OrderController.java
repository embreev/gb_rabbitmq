package com.geekbrains.decembermarket.controllers;

import com.geekbrains.decembermarket.beans.Cart;
import com.geekbrains.decembermarket.entites.Order;
import com.geekbrains.decembermarket.entites.User;
import com.geekbrains.decembermarket.rabbitmq.QueueApp;
import com.geekbrains.decembermarket.services.OrderService;
import com.geekbrains.decembermarket.services.UserService;
import com.geekbrains.decembermarket.utils.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private UserService userService;
    private OrderService orderService;
    private QueueApp queueApp;
    private Cart cart;

    @Autowired
    public OrderController(UserService userService, OrderService orderService, QueueApp queueApp, Cart cart) {
        this.userService = userService;
        this.orderService = orderService;
        this.queueApp = queueApp;
        this.cart = cart;
    }

    @GetMapping("/info")
    public String showOrderInfo(Model model, Principal principal) {
        if (principal != null) {
            User user = userService.findByPhone(principal.getName());
            model.addAttribute("def_phone", user.getPhone());
        }
        model.addAttribute("cart", cart);
        return "order_info_before_confirmation";
    }

    @PostMapping("/create")
    public String createOrder(Principal principal, Model model, @RequestParam(name = "address") String address,
                              @RequestParam("phone_number") String phone) {
        User user = null;
        if (principal != null) {
            user = userService.findByPhone(principal.getName());
        } else {
            user = userService.getAnonymousUser();
        }
        Order order = new Order(user, cart, address, phone, OrderStatus.New.name());
        order = orderService.save(order);
        try {
            queueApp.sendMessageToManager(order.getId(), order.getStatus());
        } catch (IOException e) {
            e.printStackTrace();
        }
        model.addAttribute("order_id_str", String.format("%05d", order.getId()));
        model.addAttribute("order_status", order.getStatus());
        return "order_confirmation";
    }

    @GetMapping("/history")
    public String showHistory(Model model, Principal principal) {
        User user = userService.findByPhone(principal.getName());
        model.addAttribute("username", user.getFullName());
        model.addAttribute("orders", user.getOrders());
        return "orders_history";
    }
}
