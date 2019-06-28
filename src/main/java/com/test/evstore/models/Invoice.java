package com.test.evstore.models;

import java.util.List;

public class Invoice {

    private int invoiceId;
    private double invoiceTotal;
    private List<Product> products;

    public Invoice() {
    }

    public Invoice(int invoiceId, List<Product> products) {
        this.invoiceId = invoiceId;
        this.products = products;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public double getInvoiceTotal() {
        return invoiceTotal;
    }

    public void setInvoiceTotal(double invoiceTotal) {
        this.invoiceTotal = invoiceTotal;
    }
}
