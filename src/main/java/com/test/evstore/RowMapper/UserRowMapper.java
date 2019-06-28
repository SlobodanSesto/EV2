package com.test.evstore.RowMapper;


import com.test.evstore.models.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet resultSet, int i) throws SQLException {
        User user = new User();
        user.setPerId(resultSet.getInt("per_id"));
        user.setUserEmail(resultSet.getString("user_email"));
        user.setPassword(resultSet.getString("user_pass"));
        user.setRole(resultSet.getInt("user_role"));
        return user;
    }
}
