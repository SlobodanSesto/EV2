package com.test.evstore.RowMapper;

import com.test.evstore.models.Phone;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PhoneRowMapper implements RowMapper<Phone> {


    @Override
    public Phone mapRow(ResultSet resultSet, int i) throws SQLException {
        Phone p = new Phone();
        p.setPhoneID(resultSet.getInt("phone_id"));
        p.setPersonId(resultSet.getInt("per_id"));
        p.setPhoneNum(resultSet.getString("phone"));
        return p;
    }
}
