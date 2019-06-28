package com.test.evstore.controllers;

import com.test.evstore.models.*;
import com.test.evstore.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.sql.SQLException;
import java.util.List;

@Controller
//@RequestMapping(value = "update")
public class AccountUpdateController {

    @Autowired
    PersonRepo personRepo;

    @Autowired
    PhoneRepo phoneRepo;

    @Autowired
    AddressRepo addressRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    ProductRepo productRepo;


//    @RequestMapping(value = "/update", method = RequestMethod.GET)
//    public String updateForm(Model model, HttpSession session) {
//
//        model.addAttribute("person", session.getAttribute("person"));
//        model.addAttribute("user", session.getAttribute("user"));
//        model.addAttribute("updatedPerson", new Person());
//
//        return "updateForm";
//    }

    @RequestMapping(value = "/account", method = RequestMethod.GET)
    public String account(Model model, HttpSession session) {
        if (session.getAttribute("person") != null) {
            Person p = (Person) session.getAttribute("person");
            if (p.getUser().getRole() >= 2) {
                List<Product> products = null;
                products = productRepo.getAllProducts();
                model.addAttribute("products", products);
                return "controlpanel";
            }
            return "account";
        }
        return "login";
    }

//    @RequestMapping(value = "/update", method = RequestMethod.POST)
//    public String updateInfo(@ModelAttribute @Valid Person updatedPerson,
//                             Errors errors, Model model, HttpSession session) {
//        if (errors.hasErrors()) {
//            System.out.println(errors);
//            return "update";
//        }
//        Person p = (Person) session.getAttribute("person");
//        updatedPerson.setPersonId(p.getPersonId());
//
//        try {
//            personRepo.updatePersonDetails(p.getPersonId(), updatedPerson);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println(updatedPerson.getFirstName());
//        System.out.println(updatedPerson.getLastName());
//        p.setFirstName(updatedPerson.getFirstName());
//        p.setLastName(updatedPerson.getLastName());
//        session.setAttribute("person", p);
//        return "account";
//    }

    @RequestMapping(value = "/deleteNumber", method = RequestMethod.POST)
    public String deletePhone(@RequestParam("phoneId") int phoneId, HttpSession session) {

        Person person = (Person) session.getAttribute("person");

        //HAVE TO REMOVE PHONE FROM LIST BEFORE RENDERING ACCOUNT VIEW AGAIN
        Phone phoneToRemove = null;
        for (Phone p : person.getPhoneList()) {
            if(p.getPhoneID() == phoneId) {
                phoneToRemove = p;
            }
        }
        person.getPhoneList().remove(phoneToRemove);
        System.out.println(phoneId);
        phoneRepo.deleteNumber(phoneId);

        return "account";

    }

    @RequestMapping(value = "/saveNumber", method = RequestMethod.POST)
    public String saveNumber(@RequestParam("phoneId") Integer phoneId,
                             @RequestParam("phone-num") String phone, HttpSession session) {

        Person person = (Person) session.getAttribute("person");

        if (phoneId == null) {
            Phone newPhone = new Phone(person.getPersonId(), phone);
            newPhone.setPhoneID(phoneRepo.saveNewNumber(newPhone));

            person.getPhoneList().add(newPhone);

            session.setAttribute("person", person);
            return "account";
        }

        phoneRepo.save(phone, phoneId);

        for (Phone p : person.getPhoneList()) {
            if (p.getPhoneID() == phoneId) {
                p.setPhoneNum(phone);
            }
        }

        session.setAttribute("person", person);
        return "account";
    }

    @RequestMapping(value = "/saveAddress", method = RequestMethod.POST)
    public String saveAddress(@RequestParam("addressId") Integer id,
                              @RequestParam("street-address") String street,
                              @RequestParam("city") String city,
                              @RequestParam("state") String state,
                              @RequestParam("zip") String zip, HttpSession session) {

        Person person = (Person) session.getAttribute("person");

        if (id == null) {
            Address newAddress = new Address(street,city,state,zip,person.getPersonId());
            newAddress.setAddressId(addressRepo.addAddress(newAddress));
            person.getAddressList().add(newAddress);
            session.setAttribute("person", person);

            return "account";
        }

        Address addressToUpdate = null;

        for (Address address : person.getAddressList()) {
            if (address.getAddressId() == id) {
                addressToUpdate = address;
            }
        }

        addressToUpdate.setStreet(street);
        addressToUpdate.setCity(city);
        addressToUpdate.setState(state);
        addressToUpdate.setZip(zip);

        addressRepo.save(addressToUpdate);

        session.setAttribute("person", person);

        return "account";
    }

    @RequestMapping(value = "/deleteAddress", method = RequestMethod.POST)
    public String deleteAddress(@RequestParam("addressId") int addressId, HttpSession session) {

        Person person = (Person) session.getAttribute("person");
//        HAVE TO REMOVE ADDRESS FROM LIST BEFORE RENDERING ACCOUNT VIEW AGAIN

        Address addressToRemove = null;
        for (Address a : person.getAddressList()) {
            if(a.getAddressId() == addressId) {
                addressToRemove = a;
            }
        }

        person.getAddressList().remove(addressToRemove);
//        System.out.println(addressId);
        addressRepo.deleteAddress(addressId);

        return "account";
    }

    @RequestMapping(value = "/updateUser", method = RequestMethod.POST)
    public String updateUser( @RequestParam("email") String email,
                              @RequestParam("pass") String pass,
                              @RequestParam("new-pass") String newPass,
                              @RequestParam("new-pass-conf") String newPassConf,
                              Model model, HttpSession session) {

        Person person = (Person) session.getAttribute("person");

        if (pass.equals(null) || pass.equals("")){
            model.addAttribute("msg" , "Try again , Please complete all fields this time");
            return "account";
        }else if (!pass.equals(person.getUser().getPassword()) || !newPass.equals(newPassConf)) {
            model.addAttribute("msg" , "Incorrect password, try again");
            return "account";
        }else{
            userRepo.updatePass(newPass, person.getPersonId());
            person.getUser().setPassword(newPass);
            session.setAttribute("person", person);
            return "account";
        }
    }

    @RequestMapping( value = "/findPhone/{id:[\\d]+}", method = RequestMethod.GET )
    @ResponseBody
    public Phone finePhoneById( @PathVariable("id") Integer id) {
        return phoneRepo.findPhoneById(id);
    }

    @RequestMapping( value = "/findAddress/{id:[\\d]+}", method = RequestMethod.GET )
    @ResponseBody
    public Address fineAddressById( @PathVariable("id") Integer id) {
        return addressRepo.getAddressById(id);
    }

    @RequestMapping( value = "/findUser", method = RequestMethod.GET)
    @ResponseBody
    public User findUser(HttpSession session) {
        Person person = (Person) session.getAttribute("person");
        return person.getUser();
    }

}
