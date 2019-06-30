package com.test.evstore.repositories;

import com.test.evstore.RowMapper.InvoiceRowMapper;
import com.test.evstore.RowMapper.ProductRowMapper;
import com.test.evstore.models.Address;
import com.test.evstore.models.Invoice;
import com.test.evstore.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class InvoiceRepo {

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    PersonRepo personRepo;

    //fetches existing invoice by person id, returns Invoice obj with id, total, and empty list for products
    //that should be populated before rendering by productRepo using the invoice_product table (many to many)
    public Invoice getPersonInvoice(int personId) {
        Invoice invoice = null;
        String sql = "SELECT * FROM invoice WHERE per_id=? AND state=1";
        try {
            invoice = jdbcTemplate.queryForObject(sql,(resultSet, i) -> {
                return new InvoiceRowMapper().mapRow(resultSet, i);
            }, personId);
        }catch (Exception e){
            System.out.println(e);
        }

        return invoice;
    }

    //creates a new invoice using per_id then returns PK for assignment to the invoice obj
    public int insertInvoice(int personId) {
        String sql = "INSERT INTO invoice (per_id) VALUES (?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(dataSource -> {
            PreparedStatement ps = dataSource
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, personId);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();

    }


    public void addProductToInvoice(int invoiceId, int proId) {

        String sql = "INSERT INTO invoice_product (inv_id, pro_id) VALUES (?,?);";
        try {
            jdbcTemplate.update(sql, invoiceId, proId);
            updateTotal(invoiceId);
            System.out.println("Item added to invoice");
        }catch (Exception e) {
            jdbcTemplate.update("INSERT INTO error_log (error) VALUES (?);", e.getMessage());
            System.out.println(e.getStackTrace());
        }

    }

    //returns products that are on the invoice_product table
    // for the selected invoiceId and returns it to the cart view
    public List<Product> getProductsOnInvoice(int invoiceId) {

        List<Product> products = null;

        String sql = "SELECT p.pro_id, p.pro_name, p.pro_price, p.pro_desc, p.pro_stock, p.category_id FROM product p " +
                "INNER JOIN invoice_product ip ON p.pro_id = ip.pro_id WHERE ip.inv_id=?;";

//query below didn't return multiples of a product on invoice, refactored statement above works
//and returns all products to the cart view correctly
//sql = "SELECT * FROM product p WHERE p.pro_id IN (SELECT ip.pro_id FROM invoice_product ip WHERE ip.inv_id=?);

        products = jdbcTemplate.query(sql, (resultSet, i) -> { return new ProductRowMapper().mapRow(resultSet, i);
        }, invoiceId);

        return products;
    }

    public void removeFromCart(int invoiceId, int proId) {
        String sql = "DELETE FROM invoice_product WHERE pro_id=? AND inv_id=? LIMIT 1;";
        try {
            jdbcTemplate.update(sql, proId, invoiceId);
            System.out.println("item id: "+proId+" was removed from cart id: "+invoiceId);
            updateTotal(invoiceId);
        }catch (Exception e) {
            jdbcTemplate.update("INSERT INTO error_log (error) VALUES (?);", e.getMessage());
            System.out.println(e);
        }
    }

    public int startCheckOut(int invoiceId, int personId) {

        String sql = "UPDATE invoice SET state=3 WHERE inv_id=?;";
        String orderSql = "INSERT INTO placed_order (invoice_id, add_street, add_city, add_state, add_zip) " +
                "VALUES (?, ?, ?, ?, ?);";
        try {
            jdbcTemplate.update(sql, invoiceId);
            Address a = personRepo.getPrimaryAddress(personId);
            jdbcTemplate.update(orderSql, invoiceId, a.getStreet(), a.getCity(), a.getState(), a.getZip());
        } catch ( Exception e ) {
            jdbcTemplate.update("INSERT INTO error_log (error) VALUES (?);", e.getMessage());
            System.out.println(e);
            e.printStackTrace();
            return -1;
        }
        return insertInvoice(personId);
    }

    public double getTotal(int invoiceId) {

        String sql = "SELECT inv_total FROM invoice WHERE inv_id=?";
        double total = 0;
        try {
            total = jdbcTemplate.query(sql,resultSet -> { resultSet.next(); return resultSet.getDouble("inv_total");}, invoiceId);
        } catch ( Exception e ) {
            jdbcTemplate.update("INSERT INTO error_log (error) VALUES (?);", e.getMessage());
            System.out.println(e);
            e.printStackTrace();
        }
        return total;
    }

    public void updateTotal(int invId) {
        String sql = "UPDATE invoice SET inv_total=(SELECT sum(p.pro_price) FROM product p " +
                "INNER JOIN invoice_product ip ON p.pro_id = ip.pro_id WHERE ip.inv_id=?) WHERE inv_id=?;";
        try {
            jdbcTemplate.update(sql, invId, invId);
        } catch ( Exception e ) {
            jdbcTemplate.update("INSERT INTO error_log (error) VALUES (?);", e.getMessage());
            System.out.println(e);
        }
    }

    public  Double salesStats(int cartState) {
        String sql = "SELECT sum(inv_total) AS total FROM invoice WHERE state=?";
        Double total = 0.0;
        total = jdbcTemplate.query(sql, resultSet -> { resultSet.next();
            return resultSet.getDouble("total");}, cartState);
        return total;
    }



}