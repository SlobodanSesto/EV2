package com.test.evstore.repositories;

import com.test.evstore.RowMapper.PersonRowMapper;
import com.test.evstore.models.Address;
import com.test.evstore.models.Person;
import com.test.evstore.models.Phone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@Repository
public class PersonRepo {

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    AddressRepo addressRepo;
    @Autowired
    PhoneRepo phoneRepo;

    public Person getPersonById(int personId){
        Person person = jdbcTemplate.queryForObject("SELECT * FROM person WHERE per_id=?",
                (resultSet, i) -> {return new PersonRowMapper().mapRow(resultSet,i); } , personId);

        return person;
    }

    //inserts new person from registration form returns generated PK , pk is then used to insert into user table
    //the email and pass for that user
    public int insertNewPerson(Person person) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(dataSource -> {
            PreparedStatement ps = dataSource
                    .prepareStatement("INSERT INTO person (per_name, per_lastName) VALUES ( ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, person.getFirstName());
            ps.setString(2, person.getLastName());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }


    public void updatePersonDetails(int personId, Person updatedPerson) throws SQLException {

        String sqlPerson = "UPDATE person SET per_name = ?, per_lastname = ? WHERE per_id = ?";
        try {
            jdbcTemplate.update(sqlPerson, updatedPerson.getFirstName(),updatedPerson.getLastName(), personId);
        } catch ( Exception e ) {
            jdbcTemplate.update("INSERT INTO error_log (error) VALUES (?);", e.getMessage());
            System.out.println(e);
        }

    }

    public void setPrimaryAddress(int personId, int addressId) {
        String sql = "UPDATE person SET primary_address_id=? WHERE per_id=?;";
        try {
            jdbcTemplate.update(sql, addressId, personId);
        } catch ( Exception e ) {
            jdbcTemplate.update("INSERT INTO error_log (error) VALUES (?);", e.getMessage());
            System.out.println(e);
        }
    }

    public void setPrimaryPhone(int personId, int phoneId) {
        String sql = "UPDATE person SET primary_phone_id=? WHERE per_id=?;";
        try {
            jdbcTemplate.update(sql, phoneId, personId);
        } catch ( Exception e ) {
            jdbcTemplate.update("INSERT INTO error_log (error) VALUES (?);", e.getMessage());
            System.out.println(e);
        }
    }

    public Address getPrimaryAddress(int personId) {
        String sql = "SELECT * FROM person WHERE per_id=?;";
        Integer primaryAddressId = -1;
        try {
            primaryAddressId = jdbcTemplate.query(sql,resultSet -> { resultSet.next();
                return resultSet.getInt("primary_address_id"); },personId);
        } catch ( Exception e ) {
            jdbcTemplate.update("INSERT INTO error_log (error) VALUES (?);", e.getMessage());
            System.out.println(e);
            return null;
        }
        if (primaryAddressId > 0) {
            return addressRepo.getAddressById(primaryAddressId);
        }
        return null;
    }

    public Phone getPrimaryPhone(int personId) {
        String sql = "SELECT * FROM person WHERE per_id=?;";
        Integer primaryPhoneId = -1;
        try {
            primaryPhoneId = jdbcTemplate.query(sql,resultSet -> { resultSet.next();
                return resultSet.getInt("primary_phone_id");},personId);
        } catch ( Exception e ) {
            jdbcTemplate.update("INSERT INTO error_log (error) VALUES (?);", e.getMessage());
            System.out.println(e);
            return null;
        }
        if (primaryPhoneId > 0) {
            return phoneRepo.findPhoneById(primaryPhoneId);
        }
        return null;
    }
}
