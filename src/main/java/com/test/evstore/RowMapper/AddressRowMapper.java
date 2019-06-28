package com.test.evstore.RowMapper;

import com.test.evstore.models.Address;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AddressRowMapper implements RowMapper<Address> {


    @Override
    public Address mapRow(ResultSet resultSet, int i) throws SQLException {

        Address address = new Address();

        address.setAddressId(resultSet.getInt("address_id"));
        address.setStreet(resultSet.getString("address_street"));
        address.setCity(resultSet.getString("address_city"));
        address.setState(resultSet.getString("address_state"));
        address.setZip(resultSet.getString("address_zip"));
        address.setPersonId(resultSet.getInt("per_id"));

        return address;
    }
}
