package com.tik.aipusharchiveservice.service;

import com.tik.aipusharchiveservice.bean.Person;

import java.util.List;

public interface PersonService {
    Person create(Person person);
    Person update(Person person);
    void delete(Long id);
    Person getById(Long id);
    List<Person> getAll();
    List<Person> search(Person condition);
    List<Person> getByDepartment(String department);
    long getTotalCount();
}