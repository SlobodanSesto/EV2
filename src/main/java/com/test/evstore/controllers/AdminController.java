package com.test.evstore.controllers;

import com.test.evstore.models.MyError;
import com.test.evstore.models.Person;
import com.test.evstore.repositories.InvoiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    InvoiceRepo invoiceRepo;
    @Autowired
    CartController cartController;
    @Autowired
    JdbcTemplate jdbcTemplate;

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

    @GetMapping(value = "/errorlog")
    @ResponseBody
    public List<MyError> getErrors(HttpSession session) {

        Person person = (Person) session.getAttribute("person");
        if (person.getUser().getRole() == 2) {

            List<MyError> errors = jdbcTemplate.query("SELECT * FROM error_log;", (resultSet, i) -> {
                MyError e = new MyError();
                e.setId(resultSet.getInt("err_id"));
                e.setError(resultSet.getString("error"));
                e.setTime(resultSet.getString("timestamp"));
                return e;
            });
            return errors;
        }
        return null;
    }


}