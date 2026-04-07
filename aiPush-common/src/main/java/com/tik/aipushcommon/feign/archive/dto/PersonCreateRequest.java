package com.tik.aipushcommon.feign.archive.dto;

import lombok.Data;

@Data
public class PersonCreateRequest {

    private String name;
    private String idCard;
    private String department;
    private String position;
    private String phone;
    private String email;
}
