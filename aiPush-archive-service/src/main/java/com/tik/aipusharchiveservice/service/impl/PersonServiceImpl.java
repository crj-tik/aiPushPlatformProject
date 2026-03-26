package com.tik.aipusharchiveservice.service.impl;

import com.tik.aipusharchiveservice.bean.Person;
import com.tik.aipusharchiveservice.mapper.PersonMapper;
import com.tik.aipusharchiveservice.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonMapper personMapper;

    @Override
    @Transactional
    public Person create(Person person) {
        person.setCreateTime(LocalDateTime.now());
        person.setUpdateTime(LocalDateTime.now());
        personMapper.insert(person);
        return person;
    }

    @Override
    @Transactional
    public Person update(Person person) {
        person.setUpdateTime(LocalDateTime.now());
        personMapper.updateById(person);
        return personMapper.selectById(person.getId());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        personMapper.deleteById(id);
    }

    @Override
    public Person getById(Long id) {
        return personMapper.selectById(id);
    }

    @Override
    public List<Person> getAll() {
        return personMapper.selectAll();
    }

    @Override
    public List<Person> search(Person condition) {
        return personMapper.selectByCondition(condition);
    }

    @Override
    public List<Person> getByDepartment(String department) {
        return personMapper.selectByDepartment(department);
    }

    @Override
    public long getTotalCount() {
        return personMapper.count();
    }
}

