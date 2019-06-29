package com.test.evstore.repositories;


import com.test.evstore.RowMapper.PhoneRowMapper;
import com.test.evstore.models.Phone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class PhoneRepo {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Phone> getPhoneList(int personId) {
        List<Phone> phoneList = null;

        phoneList = jdbcTemplate.query("SELECT * FROM phone WHERE per_id=?", (resultSet, i) -> {
            return new PhoneRowMapper().mapRow(resultSet,i);
        }, personId);

        return phoneList;
    }

    public Phone findPhoneById(Integer phoneId) {
        Phone phone = null;

        String sql = "SELECT * FROM phone WHERE phone_id=?";
        phone = jdbcTemplate.queryForObject(sql, (resultSet, i) -> {
            return new PhoneRowMapper().mapRow(resultSet,i);
            }, phoneId);

        return phone;
    }

    public void deleteNumber(int phoneId) {

        String sql = "DELETE FROM phone WHERE phone_id=?";
        jdbcTemplate.update(sql, phoneId);

    }

    public void save(String updatedPhone, int phoneId) {
        String sql = "UPDATE phone SET phone=? WHERE phone_id=?";
        jdbcTemplate.update(sql, updatedPhone, phoneId);
    }

    public int saveNewNumber(Phone phone) {
        String sql = "INSERT INTO phone (phone, per_id) VALUES (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(dataSource -> {
            PreparedStatement ps = dataSource
                    .prepareStatement(sql , Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, phone.getPhoneNum());
            ps.setInt(2, phone.getPersonId());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }





}
