package com.tik.aipusharchiveservice.controller;

import com.tik.aipusharchiveservice.bean.Person;
import com.tik.aipusharchiveservice.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @PostMapping
    public Person create(@RequestBody Person person) {
        return personService.create(person);
    }

    @PutMapping("/{id}")
    public Person update(@PathVariable Long id, @RequestBody Person person) {
        person.setId(id);
        return personService.update(person);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        personService.delete(id);
    }

    @GetMapping("/{id}")
    public Person getById(@PathVariable Long id) {
        return personService.getById(id);
    }

    @GetMapping
    public List<Person> getAll() {
        return personService.getAll();
    }

    @PostMapping("/search")
    public List<Person> search(@RequestBody Person condition) {
        return personService.search(condition);
    }

    @GetMapping("/department/{department}")
    public List<Person> getByDepartment(@PathVariable String department) {
        return personService.getByDepartment(department);
    }

    @GetMapping("/count")
    public long getCount() {
        return personService.getTotalCount();
    }
}