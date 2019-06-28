package com.test.evstore.RowMapper;

import com.test.evstore.models.Product;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductRowMapper implements RowMapper<Product> {
    @Override
    public Product mapRow(ResultSet resultSet, int i) throws SQLException {
        Product p = new Product();
        p.setProductId(resultSet.getInt("pro_id"));
        p.setProductName(resultSet.getString("pro_name"));
        p.setProductPrice(resultSet.getDouble("pro_price"));
        p.setProductDesc(resultSet.getString("pro_desc"));
        p.setQuantity(resultSet.getInt("pro_stock"));
        p.setCategory(resultSet.getInt("category_id"));
        return p;
    }
}
