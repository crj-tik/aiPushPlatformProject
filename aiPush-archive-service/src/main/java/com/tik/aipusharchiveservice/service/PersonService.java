package com.tik.aipusharchiveservice.service;

import com.tik.aipusharchiveservice.service.entity.PersonEntity;

import java.util.List;

public interface PersonService {

    PersonEntity create(PersonEntity personEntity);

    PersonEntity update(PersonEntity personEntity);

    void delete(Long id);

    PersonEntity getById(Long id);

    List<PersonEntity> getAll();

    List<PersonEntity> search(PersonEntity condition);

    List<PersonEntity> getByDepartment(String department);

    long getTotalCount();
}
