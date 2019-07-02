package com.test.evstore.models;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class Person {


    private int personId;
    @NotNull
    @Size(min = 2 , max = 45)
    private String firstName;
    @NotNull
    @Size(min = 2 , max = 45)
    private String lastName;

    private User user;

    private Invoice invoice;

    private List<Phone> phoneList;

    private List<Address> addressList;

    private List<Invoice> completedOrders;

    private Address primaryAddress;

    private Phone primaryPhone;


    public Person() {
    }

    public Person(@NotNull @Size(min = 2, max = 45) String firstName,
                  @NotNull @Size(min = 2, max = 45) String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Phone> getPhoneList() {
        return phoneList;
    }

    public void setPhoneList(List<Phone> phoneList) {
        this.phoneList = phoneList;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Address getPrimaryAddress() {
        return primaryAddress;
    }

    public void setPrimaryAddress(Address primaryAddress) {
        this.primaryAddress = primaryAddress;
    }

    public Phone getPrimaryPhone() {
        return primaryPhone;
    }

    public void setPrimaryPhone(Phone primaryPhone) {
        this.primaryPhone = primaryPhone;
    }

    public void setCompletedOrders(List<Invoice> completedOrders) {
        this.completedOrders = completedOrders;
    }

    public List<Invoice> getCompletedOrders() {
        return completedOrders;
    }
}
