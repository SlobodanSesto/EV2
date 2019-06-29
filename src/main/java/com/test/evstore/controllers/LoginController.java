package com.test.evstore.controllers;

import com.test.evstore.models.*;
import com.test.evstore.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;


@Controller
@RequestMapping(value = "login")
public class LoginController {

    @Autowired
    UserRepo userRepo;
    @Autowired
    PersonRepo personRepo;
    @Autowired
    PhoneRepo phoneRepo;
    @Autowired
    AddressRepo addressRepo;
    @Autowired
    ProductRepo productRepo;
    @Autowired
    InvoiceRepo invoiceRepo;

    @GetMapping
    public String login() {
        return "login";
    }

    @PostMapping
    public String loginAttempt(@RequestParam(name = "userEmail") String userEmail,
                               @RequestParam(name = "password") String password, Model model, HttpSession session) {
        String message = new String();
        User u = null;
        //checks if user exists in DB
        try {
            u = userRepo.getUser(userEmail);
        }catch (Exception e) {
            message = "Please enter a valid E-mail and password.";
            model.addAttribute("message", message);
            return "login";
        }
        //if login successful Person object gets created and additional info is fetched from DB and assigned to person
        //and person is saved in session
        //TO-DO still is add cart and some way to save is once the user logs out so its still there when they log back in
        if (u.getUserEmail().equals(userEmail) && u.getPassword().equals(password)){
            Person p = personRepo.getPersonById(u.getPerId());
            p.setPersonId(u.getPerId());
            List<Phone> phoneList = phoneRepo.getPhoneList(p.getPersonId());
            List<Address> addressList = addressRepo.getAllAddresses(p.getPersonId());
            p.setInvoice(invoiceRepo.getPersonInvoice(p.getPersonId()));
//            p.getInvoice().setInvoiceTotal(invoiceRepo.getTotal(p.getInvoice().getInvoiceId()));
            p.setUser(u);
            p.setPhoneList(phoneList);
            p.setAddressList(addressList);

            Address primaryAddress = null;
            primaryAddress = personRepo.getPrimaryAddress(p.getPersonId());
            if (primaryAddress != null) {
                p.setPrimaryAddress(primaryAddress);
            } else model.addAttribute("AddressMsg", "Please select or add a primary shipping address.");

            Phone primaryPhone = null;
            primaryPhone = personRepo.getPrimaryPhone(p.getPersonId());
            if (primaryPhone != null) {
                p.setPrimaryPhone(primaryPhone);
            } else model.addAttribute("PhoneMsg", "Please select or add a primary contact number.");

            session.setAttribute("person", p);

            //checks if admin and returns admin control panel
            if (p.getUser().getRole() == 2) {
                List<Product> products = null;
                products = productRepo.getAllProducts();
                model.addAttribute("products", products);
                return "controlpanel";
            }

            return "account";
        }

        message = "Please enter a valid E-mail and password.";
        model.addAttribute("message", message);
        return "login";
    }

    //invalidates session and returns home
    //TO-DO only display logout msg as either alert or set time , and load the normal home page with products.
    //TO-DO save any session variables that will be needed for next login, like items in cart.
    @RequestMapping(value = "logout")
    @GetMapping
    public String logOut(Model model, HttpSession session) {

        session.invalidate();
        String msg = "You have successfully logged out";
        model.addAttribute("loggedOut", msg);
        return "index";
    }

}
