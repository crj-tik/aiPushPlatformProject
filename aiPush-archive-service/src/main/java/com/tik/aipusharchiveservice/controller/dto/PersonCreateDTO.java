package com.tik.aipusharchiveservice.controller.dto;

import lombok.Data;

@Data
public class PersonCreateDTO {

    private String name;
    private String idCard;
    private String department;
    private String position;
    private String phone;
    private String email;
}
