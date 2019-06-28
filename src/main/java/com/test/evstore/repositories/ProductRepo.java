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
        jdbcTemplate.update(sql, id);
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
        jdbcTemplate.update(sql , p.getProductName(), p.getProductPrice(),
                p.getProductDesc(), p.getQuantity(), p.getCategory(), p.getProductId());
    }
}
