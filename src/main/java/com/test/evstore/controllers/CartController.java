package com.test.evstore.controllers;

import com.test.evstore.models.Person;
import com.test.evstore.repositories.InvoiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class CartController {

    @Autowired
    InvoiceRepo invoiceRepo;

    @RequestMapping(value = "/cart", method = RequestMethod.GET)
    public String cart(HttpSession session) {
        Person person = (Person) session.getAttribute("person");
        person.getInvoice().setProducts(invoiceRepo.getProductsOnInvoice(person.getInvoice().getInvoiceId()));

        return "cart";
    }

    @RequestMapping(value = "/addToCart", method = RequestMethod.POST)
    public String addToCart( @RequestParam("proId") int proId,
                             HttpSession session) {
        System.out.println(proId);
        Person person = (Person) session.getAttribute("person");
        if( person == null ) {
            return "redirect:/login";
        }
        invoiceRepo.addProductToInvoice(person.getInvoice().getInvoiceId(), proId);
        person.getInvoice().setProducts(invoiceRepo.getProductsOnInvoice(person.getInvoice().getInvoiceId()));
        return "cart";
    }

    @RequestMapping(value = "/removeFromCart", method = RequestMethod.POST)
    public String removeFromCart( @RequestParam("productId") int proId,
                                  HttpSession session) {
        Person person = (Person) session.getAttribute("person");
        invoiceRepo.removeFromCart(person.getInvoice().getInvoiceId(), proId);
        person.getInvoice().setProducts(invoiceRepo.getProductsOnInvoice(person.getInvoice().getInvoiceId()));
        return "cart";
    }

}
