package com.test.evstore.models;

public class Phone {

    private int phoneID;

    private int personId;

    private String phoneNum;

    public Phone() {
    }

    public Phone(int personId, String phoneNum) {
        this.personId = personId;
        this.phoneNum = phoneNum;
    }

    public int getPhoneID() {
        return phoneID;
    }

    public void setPhoneID(int phoneID) {
        this.phoneID = phoneID;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
