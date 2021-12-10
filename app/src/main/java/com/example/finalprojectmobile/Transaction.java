package com.example.finalprojectmobile;

import java.util.Date;

public class Transaction {

    private String productName;
    private String type;
    private String user;
    private String description;
    private String date;
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Transaction(String productName, String type, String user, String description, String date, String reason) {
        this.productName = productName;
        this.type = type;
        this.user = user;
        this.description = description;
        this.date = date;
        this.reason=reason;
    }
}
