package com.test.evstore.repositories;

import com.test.evstore.RowMapper.AddressRowMapper;
import com.test.evstore.models.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class AddressRepo {

    @Autowired
    JdbcTemplate jdbcTemplate;

    //inserts new address from account view , returns generated PK as int - datasource is in resources/app.properties
    public int addAddress(Address a){
        String sql = "INSERT INTO address (address_street, address_city, address_state, address_zip, per_id) VALUES " +
                "(?,?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(dataSource -> {
            PreparedStatement ps = dataSource
                    .prepareStatement(sql , Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, a.getStreet());
            ps.setString(2, a.getCity());
            ps.setString(3, a.getState());
            ps.setString(4, a.getZip());
            ps.setInt(5, a.getPersonId());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public List<Address> getAllAddresses(int personId) {

        List<Address> addresses = null;

        addresses = jdbcTemplate.query("SELECT * FROM address WHERE per_id=?", (resultSet, i) -> {
           return new AddressRowMapper().mapRow(resultSet,i); }, personId );

        return addresses;
    }

    public void save(Address a) {
        String sql = "UPDATE address SET address_street=?, address_city=?," +
                " address_state=?, address_zip=?, per_id=?  WHERE address_id=?";
        try {
            jdbcTemplate.update(sql, a.getStreet(), a.getCity(),
                    a.getState(), a.getZip(), a.getPersonId(), a.getAddressId());
        } catch (Exception e) {
            jdbcTemplate.update("INSERT INTO error_log (error) VALUES (?);", e.getMessage());
            System.out.println(e);
        }

    }

    public Address getAddressById(Integer addressId) {
        Address address = null;
        String sql = "SELECT * FROM address WHERE address_id=?";

        address = jdbcTemplate.queryForObject(sql, (resultSet, i) -> {
            return new AddressRowMapper().mapRow(resultSet, i);
        }, addressId);

        return address;

    }

    public void deleteAddress(int addressId) {

        String sql = "DELETE FROM address WHERE address_id=?";
        try {
            jdbcTemplate.update(sql,addressId);
        } catch ( Exception e ) {
            jdbcTemplate.update("INSERT INTO error_log (error) VALUES (?);", e.getMessage());
            System.out.println(e);
        }    }

}
