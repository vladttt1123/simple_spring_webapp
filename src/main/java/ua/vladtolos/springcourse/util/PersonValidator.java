package ua.vladtolos.springcourse.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ua.vladtolos.springcourse.dao.PersonDAO;
import ua.vladtolos.springcourse.models.Person;
import java.util.Optional;

@Component
public class PersonValidator implements Validator{

    private final PersonDAO personDAO; 

    @Autowired
    public PersonValidator(PersonDAO personDAO){
        this.personDAO = personDAO;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object o, Errors errors) {
       Person person = (Person) o; 
       //check if such person exists in db
     if(personDAO.show(person.getEmail()).isPresent()){
        errors.rejectValue("email", "", "This email is already taken");
     }
    }
    
}
