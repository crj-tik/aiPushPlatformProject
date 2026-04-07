package com.tik.aipusharchiveservice.controller;

import com.tik.aipusharchiveservice.controller.dto.PersonCreateDTO;
import com.tik.aipusharchiveservice.controller.dto.PersonQueryDTO;
import com.tik.aipusharchiveservice.controller.dto.PersonUpdateDTO;
import com.tik.aipusharchiveservice.controller.vo.PersonVO;
import com.tik.aipusharchiveservice.convertor.PersonConvertor;
import com.tik.aipusharchiveservice.service.PersonService;
import com.tik.aipusharchiveservice.service.entity.PersonEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @PostMapping
    public PersonVO create(@RequestBody PersonCreateDTO createDTO) {
        PersonEntity personEntity = personService.create(PersonConvertor.toEntity(createDTO));
        return PersonConvertor.toVO(personEntity);
    }

    @PutMapping("/{id}")
    public PersonVO update(@PathVariable Long id, @RequestBody PersonUpdateDTO updateDTO) {
        PersonEntity personEntity = PersonConvertor.toEntity(updateDTO);
        personEntity.setId(id);
        return PersonConvertor.toVO(personService.update(personEntity));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        personService.delete(id);
    }

    @GetMapping("/{id}")
    public PersonVO getById(@PathVariable Long id) {
        return PersonConvertor.toVO(personService.getById(id));
    }

    @GetMapping
    public List<PersonVO> getAll() {
        return PersonConvertor.toVOList(personService.getAll());
    }

    @PostMapping("/search")
    public List<PersonVO> search(@RequestBody PersonQueryDTO condition) {
        return PersonConvertor.toVOList(personService.search(PersonConvertor.toEntity(condition)));
    }

    @GetMapping("/department/{department}")
    public List<PersonVO> getByDepartment(@PathVariable String department) {
        return PersonConvertor.toVOList(personService.getByDepartment(department));
    }

    @GetMapping("/count")
    public long getCount() {
        return personService.getTotalCount();
    }
}
