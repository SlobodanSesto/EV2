package com.test.evstore.repositories;

import com.test.evstore.RowMapper.UserRowMapper;
import com.test.evstore.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepo {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public User getUser( String userEmail ){
//        String sql = "SELECT user_id, user_name, user_pass FROM user WHERE user_name=?";
//        User u = jdbcTemplate.queryForObject(sql, new UserRowMapper(),userName);
//
//        return u;
        User user = jdbcTemplate.queryForObject("SELECT per_id, user_email, user_pass, user_role FROM user WHERE user_email=?",
                (resultSet, i) -> { return new UserRowMapper().mapRow(resultSet, i); },userEmail);
        return user;
    }

    public void insertNewUser(User user, int personId) {
        String sql = "INSERT INTO user (user_email, user_pass, per_id) VALUES ( ?, ?, ?)";
        jdbcTemplate.update(sql, user.getUserEmail(), user.getPassword(), personId);
    }

    public void updatePass(String p , int id) {
        String sql = "UPDATE user SET user_pass=? WHERE per_id=?";
        jdbcTemplate.update(sql, p, id);
    }
}
