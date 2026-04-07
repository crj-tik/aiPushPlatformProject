package com.tik.aipusharchiveservice.service.impl;

import com.tik.aipusharchiveservice.convertor.PersonConvertor;
import com.tik.aipusharchiveservice.dal.dataobject.PersonDO;
import com.tik.aipusharchiveservice.dal.mapper.PersonMapper;
import com.tik.aipusharchiveservice.service.PersonService;
import com.tik.aipusharchiveservice.service.entity.PersonEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonMapper personMapper;

    @Override
    @Transactional
    public PersonEntity create(PersonEntity personEntity) {
        LocalDateTime now = LocalDateTime.now();
        personEntity.setCreateTime(now);
        personEntity.setUpdateTime(now);
        PersonDO personDO = PersonConvertor.toDO(personEntity);
        personMapper.insertSelective(personDO);
        return PersonConvertor.toEntity(personDO);
    }

    @Override
    @Transactional
    public PersonEntity update(PersonEntity personEntity) {
        personEntity.setUpdateTime(LocalDateTime.now());
        personMapper.updateByPrimaryKeySelective(PersonConvertor.toDO(personEntity));
        return getById(personEntity.getId());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        personMapper.deleteByPrimaryKey(id);
    }

    @Override
    public PersonEntity getById(Long id) {
        return PersonConvertor.toEntity(personMapper.selectByPrimaryKey(id));
    }

    @Override
    public List<PersonEntity> getAll() {
        return personMapper.selectAll().stream()
                .map(PersonConvertor::toEntity)
                .toList();
    }

    @Override
    public List<PersonEntity> search(PersonEntity condition) {
        if (condition == null) {
            return getAll();
        }
        Example example = new Example(PersonDO.class);
        Example.Criteria criteria = example.createCriteria();
        if (hasText(condition.getName())) {
            criteria.andLike("name", "%" + condition.getName().trim() + "%");
        }
        if (hasText(condition.getDepartment())) {
            criteria.andEqualTo("department", condition.getDepartment().trim());
        }
        if (hasText(condition.getPosition())) {
            criteria.andEqualTo("position", condition.getPosition().trim());
        }
        if (hasText(condition.getIdCard())) {
            criteria.andEqualTo("idCard", condition.getIdCard().trim());
        }
        example.orderBy("id").desc();
        return personMapper.selectByExample(example).stream()
                .map(PersonConvertor::toEntity)
                .toList();
    }

    @Override
    public List<PersonEntity> getByDepartment(String department) {
        if (!hasText(department)) {
            return getAll();
        }
        Example example = new Example(PersonDO.class);
        example.createCriteria().andEqualTo("department", department.trim());
        example.orderBy("id").desc();
        return personMapper.selectByExample(example).stream()
                .map(PersonConvertor::toEntity)
                .toList();
    }

    @Override
    public long getTotalCount() {
        return personMapper.selectCount(new PersonDO());
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}

