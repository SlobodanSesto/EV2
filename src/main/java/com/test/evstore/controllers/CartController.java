package com.test.evstore.controllers;


import com.test.evstore.models.Person;
import com.test.evstore.models.Product;
import com.test.evstore.repositories.InvoiceRepo;
import com.test.evstore.repositories.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.util.List;

@Controller
public class CartController {

    @Autowired
    InvoiceRepo invoiceRepo;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    ProductRepo productRepo;

    @RequestMapping(value = "/cart", method = RequestMethod.GET)
    public String cart(HttpSession session) {
        Person person = (Person) session.getAttribute("person");
        person.getInvoice().setProducts(invoiceRepo.getProductsOnInvoice(person.getInvoice().getInvoiceId()));
        person.getInvoice().setInvoiceTotal(priceFormat(invoiceRepo.getTotal(person.getInvoice().getInvoiceId())));
        session.setAttribute("person", person);
        return "cart";
    }

    @RequestMapping(value = "/addToCart", method = RequestMethod.POST)
    public String addToCart(@RequestParam("proId") int proId,
                            HttpSession session, Model model) {
        System.out.println(proId);
        Person person = (Person) session.getAttribute("person");
        if (person == null) {
            model.addAttribute("notLoggedIn", "Please sign in before adding items to cart or wishlist");
            return "index";
        }
        invoiceRepo.addProductToInvoice(person.getInvoice().getInvoiceId(), proId);
        person.getInvoice().setProducts(invoiceRepo.getProductsOnInvoice(person.getInvoice().getInvoiceId()));
        person.getInvoice().setInvoiceTotal(updateTotalOnInvoice(person.getInvoice().getProducts()));
        session.setAttribute("person", person);
        return "cart";
    }

    @RequestMapping(value = "/addToWishlist", method = RequestMethod.POST)
    public String addToWishlist(@RequestParam("proIdWishlist") int proId,
                                HttpSession session, Model model) {

        Person person = (Person) session.getAttribute("person");
        if (person == null) {
            model.addAttribute("notLoggedIn", "Please sign in before adding items to cart or wishlist");
            return "index";
        }

        invoiceRepo.addProductToInvoice(person.getWishlist().getInvoiceId(), proId);
        person.getWishlist().setProducts(invoiceRepo.getProductsOnInvoice(person.getWishlist().getInvoiceId()));
        session.setAttribute("person", person);

        return "cart";
    }

    @RequestMapping(value = "/removeFromWishlist", method = RequestMethod.POST)
    public String removeFromWishlist(@RequestParam("productId") int proId,
                                     HttpSession session) {
        Person person = (Person) session.getAttribute("person");
        invoiceRepo.removeFromCart(person.getWishlist().getInvoiceId(), proId);
        person.getWishlist().setProducts(invoiceRepo.getProductsOnInvoice(person.getWishlist().getInvoiceId()));
        session.setAttribute("person", person);
        return "cart";
    }

    @RequestMapping(value = "/removeFromCart", method = RequestMethod.POST)
    public String removeFromCart(@RequestParam("productId") int proId,
                                 HttpSession session) {
        Person person = (Person) session.getAttribute("person");
        invoiceRepo.removeFromCart(person.getInvoice().getInvoiceId(), proId);
        person.getInvoice().setProducts(invoiceRepo.getProductsOnInvoice(person.getInvoice().getInvoiceId()));
        person.getInvoice().setInvoiceTotal(updateTotalOnInvoice(person.getInvoice().getProducts()));
        session.setAttribute("person", person);
        return "cart";
    }

    //function imitates a transaction where either all the products in cart can be purchased and are in stock
    // or the process gets reversed if at any point product stock becomes <= 0.
    @RequestMapping(value = "/checkOut", method = RequestMethod.POST)
    public String checkOut(HttpSession session, Model model) {

        Person person = (Person) session.getAttribute("person");
        person.getInvoice().setProducts(invoiceRepo.getProductsOnInvoice(person.getInvoice().getInvoiceId()));
        //bug fix - needed a check to see if cart is empty and if so break
        if (person.getInvoice().getProducts().isEmpty()){
            model.addAttribute("emptyCart", "Please add items to cart before checking out.");
            System.out.println("empty cart");
            return "cart";
        }
        //exit back to account page if primary address is not set
        if (person.getPrimaryAddress() != null) {
            //done !! -todo currently function doesnt know when there are multiples of a product in cart and doesnt update
            //stock correctly in the model, and because of that it keeps updating the database when the model has
            //stock of >= 1 but there are 2 of the same product
            //
            //check if products are in stock before processing order
            //
            //keeps track of position in the list of products in case the quantity
            // needs to be reversed when a product isn't in stock
            int counter = 0;
            for (Product p : person.getInvoice().getProducts()) {
                //fetches the most up-to-date quantity in DB
                int currentStock = productRepo.getProductStock(p.getProductId());
                //while iterating through the cart if the quantity falls below (0) the loop to reverse stock updates
                // should be executed to reverse state of products in stock to before attempted checkout
                if (currentStock - 1 < 0) {
                    for (Product pReverse : person.getInvoice().getProducts()) {
                        if ( counter > 0 ) {
                            productRepo.reverseStock(pReverse.getProductId());
                            p.setQuantity(p.getQuantity()+1);
                            counter--;
                        }
                    }
                    //once the stock changes are reversed, cart page is rendered with msg that item is currently out of stock.
                    model.addAttribute("OutOfStock", p.getProductName() + " is currently out of stock.");
                    model.addAttribute("outOfStockProduct", p);
                    return "cart";
                } else {
                    productRepo.updateStock(p.getProductId());
                    p.setQuantity(p.getQuantity()-1);
                    counter++;
                }
            }

            try {
                int newInv = invoiceRepo.startCheckOut(person.getInvoice().getInvoiceId(), person.getPersonId());
                //function above will return -1 if there was an error during checkout
                if (newInv > 0) {
                    person.getInvoice().setInvoiceId(newInv);
                    model.addAttribute("Success", "Thank you for shopping with us!");
                }
            } catch (Exception e) {
                model.addAttribute("Fail", "There was a problem processing your order, please try again");
                jdbcTemplate.update("INSERT INTO error_log (error) VALUES (?);", e.getMessage());
                System.out.println(e);
            }
        } else {
            model.addAttribute("MissingAddress", "Please select a primary address before checking out.");
            return "account";
        }
        return "checkout";
    }

    //helper function adds up the total for current items in cart/invoice
    public double updateTotalOnInvoice(List<Product> products) {
        double total = 0;
        for (Product p : products) {
            total += p.getProductPrice();
        }
        return priceFormat(total);
    }

    public double priceFormat(double a) {
        DecimalFormat df = new DecimalFormat("####0.00");
        return Double.parseDouble(df.format(a));
    }
}
