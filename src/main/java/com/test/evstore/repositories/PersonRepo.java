package com.test.evstore.repositories;

import com.test.evstore.RowMapper.PersonRowMapper;
import com.test.evstore.models.Person;
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
//        String sqlPhone = "UPDATE phone SET phone = ? WHERE per_id = ?";
//        String sqlAddress = "UPDATE address SET address_street=?, address_city=?, address_state=?, address_zip=? WHERE per_id=?";
        jdbcTemplate.update(sqlPerson, updatedPerson.getFirstName(),updatedPerson.getLastName(), personId);
//        jdbcTemplate.update(sqlPhone, updatedPerson.getPhoneList().get(0),personId);

    }
}
