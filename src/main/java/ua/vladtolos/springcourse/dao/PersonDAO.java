package ua.vladtolos.springcourse.dao;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ua.vladtolos.springcourse.models.Person;

@Component
public class PersonDAO {

     private final JdbcTemplate jdbcTemplate;

     @Autowired
     public PersonDAO(JdbcTemplate jdbcTemplate) {
          this.jdbcTemplate = jdbcTemplate;
     }

     public List<Person> index() {
          return jdbcTemplate.query("select * from person", new BeanPropertyRowMapper<>(Person.class));
     }


     public Optional<Person> show(String email){
          return jdbcTemplate.query("select * from person where email = ?", new Object[] {email}, 
                                                  new BeanPropertyRowMapper<>(Person.class)).stream().findAny();
     }

     public Person show(int id) throws SQLException {
          return jdbcTemplate
                    .query("select * from person where id=?", new Object[] { id },
                              new BeanPropertyRowMapper<>(Person.class))
                    .stream().findAny().orElse(null);
     }

     public void save(Person person) throws SQLException {
          jdbcTemplate.update("insert into person (name, age, email, address) values(?, ?, ?, ?)",
                    person.getName(), person.getAge(), person.getEmail(), person.getAddress());
     }

     public void update(int id, Person updatedPerson) {
          jdbcTemplate.update("UPDATE Person SET name=?, age=?, email=?, address=? WHERE id=?", updatedPerson.getName(),
                    updatedPerson.getAge(), updatedPerson.getEmail(), updatedPerson.getAddress(), id);
     }

     public void delete(int id) throws SQLException {
          jdbcTemplate.update("delete from person where id=?", id);
     }

     /// Testing Performance of BATCH UPDATE /////

     public void testMultipleUpdate() {
          List<Person> people = create1000People();
          long before = System.currentTimeMillis();

          for (Person person : people) {
               jdbcTemplate.update("insert into person values(?, ?, ?, ?)", person.getId(),
                         person.getName(), person.getAge(), person.getEmail());
          }

          long after = System.currentTimeMillis();
          System.out.println("Time: " + (after - before));
     }

     public void testBatchUpdate() {
          List<Person> people = create1000People();
          long before = System.currentTimeMillis();

          jdbcTemplate.batchUpdate("insert into person values(?, ?, ?, ?)", new BatchPreparedStatementSetter() {

               @Override
               public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, people.get(i).getId());
                    ps.setString(2, people.get(i).getName());
                    ps.setInt(3, people.get(i).getAge());
                    ps.setString(4, people.get(i).getEmail());
               }

               @Override
               public int getBatchSize() {
                    return people.size();
               }

          });
          long after = System.currentTimeMillis();
          System.out.println("Time: " + (after - before));
     }

     private List<Person> create1000People() {
          List<Person> people = new ArrayList<>();

          for (int i = 0; i < 1000; i++) {
               people.add(new Person(i, "Name " + i, 30, "test" + i + "@ukr.net", "some address"));
          }
          return people;
     }

}
