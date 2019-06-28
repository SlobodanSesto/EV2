package com.test.evstore.controllers;

import com.test.evstore.models.*;
import com.test.evstore.repositories.InvoiceRepo;
import com.test.evstore.repositories.PersonRepo;
import com.test.evstore.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;

@Controller
@RequestMapping(value = "registration")
public class RegistrationController {

    @Autowired
    PersonRepo personRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    InvoiceRepo invoiceRepo;

    @GetMapping
    public String registration(Model model) {
        model.addAttribute(new Person());
        model.addAttribute(new User());
        return "registration";
    }

    @PostMapping
    public String registrationPost(@ModelAttribute @Valid Person person,
                                   @ModelAttribute @Valid User user,
                                   Errors errors, Model model, HttpSession session) {
        if (errors.hasErrors()) {
            System.out.println(errors);
            return "registration";
        }
        person.setPersonId(personRepo.insertNewPerson(person));
        userRepo.insertNewUser(user, person.getPersonId());
        user.setPerId(person.getPersonId());
        person.setPhoneList(new ArrayList<Phone>());
        person.setAddressList(new ArrayList<Address>());
        person.setUser(user);
        person.setInvoice(new Invoice());
        person.getInvoice().setInvoiceId(invoiceRepo.insertInvoice(person.getPersonId()));
        session.setAttribute("userId", person.getPersonId());
        session.setAttribute("person", person);
        return "account";
    }

//    The method above creates a new object and assigns form input to the object fields if valid.
//    Using the java validation api annotations
//    @PostMapping
//    public String registrationPost(@RequestParam("fName") String fName,
//                                   @RequestParam("lName") String lName,
//                                   @RequestParam("userName") String userName,
//                                   @RequestParam("passWord") String passWord,
//                                   @RequestParam("passWordConf") String passWordConf,
//                                   @RequestParam("email") String email,
//                                   @RequestParam("phone") String phone) {
//
//        System.out.println(fName + " | " + lName + " | " + userName + " | " + passWord + " | " + passWordConf
//                + " | " + email + " | " + phone );
//
//        return "index";
//    }


}
