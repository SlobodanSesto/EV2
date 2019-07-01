package com.test.evstore.repositories;

import com.test.evstore.RowMapper.ProductRowMapper;
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
public class ProductRepo {

    //automatically detects and creates an instance
    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<String> getAllProductNames() {
        List<String> productList = new ArrayList<>();
        productList.addAll(jdbcTemplate.queryForList("SELECT pro_name FROM product;", String.class));
        return productList;
    }

    public List<Product> getAllProducts() {
        List<Product> products = null;
        products = jdbcTemplate.query("SELECT * FROM product", (resultSet, i) -> {
            return new ProductRowMapper().mapRow(resultSet, i);
        });
        return products;
    }

    public List<Product> searchProducts(String searchParam) {
        List<Product> products = null;
        String sql = "SELECT * FROM product WHERE pro_name LIKE ? OR pro_desc LIKE ?";


        products = jdbcTemplate.query(sql, (resultSet, i) -> { return new ProductRowMapper().mapRow(resultSet, i);
        } , "%"+searchParam+"%", "%"+searchParam+"%");

        return products;
    }

    public List<Product> findByCategory(int category) {
        List<Product> products = null;
        String sql = "SELECT * FROM product WHERE category_id=?";
        products = jdbcTemplate.query( sql, (resultSet, i) -> {
            return new ProductRowMapper().mapRow(resultSet, i);
        }, category);
        return products;
    }

    public void deleteProductById(int id) {
        String sql = "DELETE FROM product WHERE pro_id=?";
        try {
            jdbcTemplate.update(sql, id);
        } catch (Exception e) {
            jdbcTemplate.update("INSERT INTO error_log (error) VALUES (?);", e.getMessage());
            System.out.println(e);
        }
    }

    public Product getProductById(int id) {
        String sql = "SELECT * FROM product WHERE pro_id=?";
        Product p = null;
        p = jdbcTemplate.queryForObject(sql, (resultSet, i) -> {
            return new ProductRowMapper().mapRow(resultSet, i);}, id);
        return p;
    }

    public Integer addProduct(Product p) {
        String sql = "INSERT INTO product (pro_name, pro_price, pro_desc, pro_stock, category_id) VALUES (?,?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(dataSource -> {
            PreparedStatement ps = dataSource
                    .prepareStatement(sql , Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, p.getProductName());
            ps.setDouble(2, p.getProductPrice());
            ps.setString(3, p.getProductDesc());
            ps.setInt(4, p.getQuantity());
            ps.setInt(5, p.getCategory());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public void updateProduct(Product p) {
        String sql = "UPDATE product SET pro_name=?, pro_price=?, pro_desc=?, pro_stock=?, category_id=? WHERE pro_id=?";
        try{
            jdbcTemplate.update(sql , p.getProductName(), p.getProductPrice(),
                    p.getProductDesc(), p.getQuantity(), p.getCategory(), p.getProductId());
        } catch (Exception e) {
            jdbcTemplate.update("INSERT INTO error_log (error) VALUES (?);", e.getMessage());
            System.out.println(e);
        }

    }

    //gets current number of product in stock
    //function is used during the checkout process to get the most up to date quantity in stock
    public int getProductStock(int id) {
        String sql = "SELECT pro_stock FROM product WHERE pro_id=?";
        int count = jdbcTemplate.query(sql,resultSet -> { resultSet.next(); return resultSet.getInt("pro_stock");}, id);
        return count;
    }

    //updates the quantity (-1) for given product_id and returns updated stock
    //updated number will be used to check if item is in stock to ship when order is being processed
    public void updateStock(int id) {
        String sql = "UPDATE product SET pro_stock=? WHERE pro_id=?";
        int stock = getProductStock(id)-1;
        try {
            jdbcTemplate.update(sql, stock, id);
        } catch ( Exception e ) {
            jdbcTemplate.update("INSERT INTO error_log (error) VALUES (?);", e.getMessage());
            System.out.println(e);
        }
    }

    //if there was a problem during checkout with a product not being in stock this function
    // would reverse the updated stock in the product table to return to state before attempted checkout
    public void reverseStock(int id) {
        String sql = "UPDATE product SET pro_stock=? WHERE pro_id=?";
        int stock = getProductStock(id)+1;
        try {
            jdbcTemplate.update(sql, stock, id);
        } catch ( Exception e ) {
            jdbcTemplate.update("INSERT INTO error_log (error) VALUES (?);", e.getMessage());
            System.out.println(e);
        }
    }

    // updates the featured column in  the product table , which is used to display featured products on home view
    public void toggleFeatured(int proId, int i) {
        String sql = "UPDATE product SET featured=? WHERE pro_id=?";
        try {
            jdbcTemplate.update(sql, i, proId);
        } catch ( Exception e ) {
            jdbcTemplate.update("INSERT INTO error_log (error) VALUES (?);", e.getMessage());
            System.out.println(e);
        }
    }

    public List<Product> getFeatured() {
        List<Product> products = null;
        String sql = "SELECT * FROM product WHERE featured=2";
        products = jdbcTemplate.query( sql, (resultSet, i) -> {
            return new ProductRowMapper().mapRow(resultSet, i);
        });
        return products;
    }

}
