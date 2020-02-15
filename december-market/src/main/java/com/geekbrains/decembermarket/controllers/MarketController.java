package com.geekbrains.decembermarket.controllers;

import com.geekbrains.decembermarket.entites.*;
import com.geekbrains.decembermarket.repositories.OrderItemRepository;
import com.geekbrains.decembermarket.services.CategoryService;
import com.geekbrains.decembermarket.services.OrderService;
import com.geekbrains.decembermarket.services.ProductService;
import com.geekbrains.decembermarket.services.UserService;
import com.geekbrains.decembermarket.beans.Cart;
import com.geekbrains.decembermarket.utils.ProductFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
public class MarketController {
    private ProductService productService;
    private CategoryService categoryService;
    private UserService userService;
    private OrderService orderService;
    private Cart cart;

    public MarketController(ProductService productService, CategoryService categoryService, UserService userService, OrderService orderService, Cart cart) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.orderService = orderService;
        this.cart = cart;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login_page";
    }

    @GetMapping("/profile")
    public String profilePage(Model model, @CookieValue(value = "data", required = false) String data, Principal principal) {
        if (principal == null) {
            return "redirect:/";
        }
        User user = userService.findByPhone(principal.getName());
        model.addAttribute("user", user);

        if (data != null) {
            System.out.println(data);
        }

        return "profile";
    }

    @GetMapping("/")
    public String index(Model model, @RequestParam Map<String, String> params) {
        int pageIndex = 0;
        if (params.containsKey("p")) {
            pageIndex = Integer.parseInt(params.get("p")) - 1;
        }
        Pageable pageRequest = PageRequest.of(pageIndex, 10);
        ProductFilter productFilter = new ProductFilter(params);
        Page<Product> page = productService.findAll(productFilter.getSpec(), pageRequest);

        List<Category> categories = categoryService.getAll();
        model.addAttribute("filtersDef", productFilter.getFilterDefinition());
        model.addAttribute("categories", categories);
        model.addAttribute("page", page);

        return "index";
    }



    // todo куки не хотят удаляться (sharing на все приложение)
    @GetMapping("/cookie")
    @ResponseBody
    public String cookie(HttpServletRequest request, @CookieValue(value = "data1", required = false) String data, HttpServletResponse response) {
        if (data == null) {
            Cookie c = new Cookie("data1", "hello");
            c.setPath("/market");
            response.addCookie(c);
            return "cookie data is empty. data cookie added";
        } else {
            return "cookie: " + data;
        }
    }

    @GetMapping("/cookie/reset")
    @ResponseBody
    public String resetCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie c = new Cookie("data1", "");
        c.setPath("/market");
        c.setMaxAge(0);
        response.addCookie(c);
        return "reset ok";
    }
}