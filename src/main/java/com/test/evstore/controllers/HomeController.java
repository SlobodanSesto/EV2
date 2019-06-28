package com.test.evstore.controllers;


import com.test.evstore.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    ProductController productController;

    @RequestMapping(value = "")
    public String home(Model model) {
        //autowired productcontroller returns all the products
        //to be displayed on the home page as "featured products"
        List<Product> productList = productController.getAllProducts();
        model.addAttribute("productList" , productList);
//        List<String> products = productController.getAllProductNames();
//        model.addAttribute("products" , products);
        return "index";
    }

}
