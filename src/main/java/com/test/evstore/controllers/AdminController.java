package com.test.evstore.controllers;

import com.test.evstore.models.Person;
import com.test.evstore.repositories.InvoiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

@Controller
public class AdminController {

    @Autowired
    InvoiceRepo invoiceRepo;
    @Autowired
    CartController cartController;

    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public String getStats(HttpSession session , Model model) {

        Person person = (Person) session.getAttribute("person");
        if (person.getUser().getRole() == 2) {
            model.addAttribute("TotalSales", String.valueOf(cartController.priceFormat(invoiceRepo.salesStats(3))));
            model.addAttribute("PendingSales", String.valueOf(invoiceRepo.salesStats(1)));

            return "controlpanel";
        }

        return "index";
    }

}