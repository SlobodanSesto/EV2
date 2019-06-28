package com.test.evstore.RowMapper;

import com.test.evstore.models.Person;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonRowMapper implements RowMapper<Person> {
    @Override
    public Person mapRow(ResultSet resultSet, int i) throws SQLException {
        Person person = new Person();

        person.setPersonId(resultSet.getInt("per_id"));
        person.setFirstName(resultSet.getString("per_name"));
        person.setLastName(resultSet.getString("per_lastname"));

        return person;
    }
}
