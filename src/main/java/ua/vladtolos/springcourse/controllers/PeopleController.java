package ua.vladtolos.springcourse.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;

import javax.validation.Valid;

import ua.vladtolos.springcourse.dao.PersonDAO;
import ua.vladtolos.springcourse.models.Person;
import ua.vladtolos.springcourse.util.PersonValidator;

@Controller
@RequestMapping("/people")
public class PeopleController {

    private final PersonDAO personDAO; 
    private final PersonValidator personValidator;

    @Autowired  // spring will inject DAO object into controller
    public PeopleController(PersonDAO personDAO, PersonValidator personValidator){
        this.personDAO = personDAO; 
        this.personValidator = personValidator;
    }

    @GetMapping("")
    public String index(Model model ){
        // get all people from dao and pass it to the view
        model.addAttribute("people", personDAO.index());
        return "people/index"; 
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model)throws SQLException{
        //get 1 person by his id from dao and pass to view
        model.addAttribute("person", personDAO.show(id));
        return "people/show";
    }

    @GetMapping("/new")
    public String newPerson(@ModelAttribute("person") Person person){
        // model.addAttribute("person", new Person());
        return "people/new";
    }

    @PostMapping
    public String create(@ModelAttribute("person") @Valid Person person, BindingResult bindingresult) throws SQLException{

        personValidator.validate(person, bindingresult);
        if(bindingresult.hasErrors()){
            return "people/new"; 
        }
        personDAO.save(person);
        return "redirect:/people";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id)throws SQLException{
        model.addAttribute("person", personDAO.show(id));
        return "people/edit"; 
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("person") @Valid Person person, BindingResult bindingresult, @PathVariable("id") int id)throws SQLException{
        personValidator.validate(person, bindingresult);
        if(bindingresult.hasErrors()){
            return "people/edit"; 
        }       
        personDAO.update(id, person);
        return "redirect:/people";
    }
    
    @DeleteMapping
    public String delete(@PathVariable("id") int id)throws SQLException{
        personDAO.delete(id);
        return "redirect:/people";
    }
    
}
