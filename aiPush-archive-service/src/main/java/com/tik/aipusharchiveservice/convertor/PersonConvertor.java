package com.tik.aipusharchiveservice.convertor;

import com.tik.aipusharchiveservice.controller.dto.PersonCreateDTO;
import com.tik.aipusharchiveservice.controller.dto.PersonQueryDTO;
import com.tik.aipusharchiveservice.controller.dto.PersonUpdateDTO;
import com.tik.aipusharchiveservice.controller.vo.PersonVO;
import com.tik.aipusharchiveservice.dal.dataobject.PersonDO;
import com.tik.aipusharchiveservice.service.entity.PersonEntity;

import java.util.List;
import java.util.Objects;

public final class PersonConvertor {

    private PersonConvertor() {
    }

    public static PersonEntity toEntity(PersonCreateDTO createDTO) {
        if (createDTO == null) {
            return null;
        }
        PersonEntity entity = new PersonEntity();
        entity.setName(createDTO.getName());
        entity.setIdCard(createDTO.getIdCard());
        entity.setDepartment(createDTO.getDepartment());
        entity.setPosition(createDTO.getPosition());
        entity.setPhone(createDTO.getPhone());
        entity.setEmail(createDTO.getEmail());
        return entity;
    }

    public static PersonEntity toEntity(PersonUpdateDTO updateDTO) {
        if (updateDTO == null) {
            return null;
        }
        PersonEntity entity = new PersonEntity();
        entity.setName(updateDTO.getName());
        entity.setIdCard(updateDTO.getIdCard());
        entity.setDepartment(updateDTO.getDepartment());
        entity.setPosition(updateDTO.getPosition());
        entity.setPhone(updateDTO.getPhone());
        entity.setEmail(updateDTO.getEmail());
        return entity;
    }

    public static PersonEntity toEntity(PersonQueryDTO queryDTO) {
        if (queryDTO == null) {
            return null;
        }
        PersonEntity entity = new PersonEntity();
        entity.setName(queryDTO.getName());
        entity.setIdCard(queryDTO.getIdCard());
        entity.setDepartment(queryDTO.getDepartment());
        entity.setPosition(queryDTO.getPosition());
        return entity;
    }

    public static PersonDO toDO(PersonEntity entity) {
        if (entity == null) {
            return null;
        }
        PersonDO personDO = new PersonDO();
        personDO.setId(entity.getId());
        personDO.setName(entity.getName());
        personDO.setIdCard(entity.getIdCard());
        personDO.setDepartment(entity.getDepartment());
        personDO.setPosition(entity.getPosition());
        personDO.setPhone(entity.getPhone());
        personDO.setEmail(entity.getEmail());
        personDO.setCreateTime(entity.getCreateTime());
        personDO.setUpdateTime(entity.getUpdateTime());
        return personDO;
    }

    public static PersonEntity toEntity(PersonDO personDO) {
        if (personDO == null) {
            return null;
        }
        PersonEntity entity = new PersonEntity();
        entity.setId(personDO.getId());
        entity.setName(personDO.getName());
        entity.setIdCard(personDO.getIdCard());
        entity.setDepartment(personDO.getDepartment());
        entity.setPosition(personDO.getPosition());
        entity.setPhone(personDO.getPhone());
        entity.setEmail(personDO.getEmail());
        entity.setCreateTime(personDO.getCreateTime());
        entity.setUpdateTime(personDO.getUpdateTime());
        return entity;
    }

    public static PersonVO toVO(PersonEntity entity) {
        if (entity == null) {
            return null;
        }
        PersonVO personVO = new PersonVO();
        personVO.setId(entity.getId());
        personVO.setName(entity.getName());
        personVO.setIdCard(entity.getIdCard());
        personVO.setDepartment(entity.getDepartment());
        personVO.setPosition(entity.getPosition());
        personVO.setPhone(entity.getPhone());
        personVO.setEmail(entity.getEmail());
        personVO.setCreateTime(entity.getCreateTime());
        personVO.setUpdateTime(entity.getUpdateTime());
        return personVO;
    }

    public static List<PersonVO> toVOList(List<PersonEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }
        return entities.stream()
                .filter(Objects::nonNull)
                .map(PersonConvertor::toVO)
                .toList();
    }
}
