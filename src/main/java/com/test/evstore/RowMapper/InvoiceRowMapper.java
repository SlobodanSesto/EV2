package com.test.evstore.RowMapper;

import com.test.evstore.models.Invoice;
import com.test.evstore.models.Product;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InvoiceRowMapper implements RowMapper<Invoice> {

    @Override
    public Invoice mapRow(ResultSet resultSet, int i) throws SQLException {

        Invoice invoice = new Invoice();
        List<Product> products = null;
        invoice.setInvoiceId(resultSet.getInt("inv_id"));
        invoice.setInvoiceTotal(resultSet.getDouble("inv_total"));
        invoice.setProducts(products);

        return invoice;
    }
}
