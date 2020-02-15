package com.geekbrains.decembermarket.soap;

import com.geekbrains.decembermarket.entites.Product;
import com.geekbrains.decembermarket.services.ProductService;
import com.geekbrains.decembermarket.soap.catalog.ProductDto;
import com.geekbrains.decembermarket.soap.catalog.ProductsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class SoapCatalogService {
    private List<ProductDto> productsDto;
    private ProductService productService;

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @PostConstruct
    public void init() {
        List<Product> products = productService.findAll();
        productsDto = new ArrayList<>();
        for (Product product : products) {
            ProductDto productDto = new ProductDto();
            productDto.setTitle(product.getTitle());
            productDto.setPrice(product.getPrice().doubleValue());
            productsDto.add(productDto);
        }
    }

    public ProductsList getAllProductsList() {
        ProductsList productsList = new ProductsList();
        productsList.getProduct().addAll(productsDto);
        return productsList;
    }
}
