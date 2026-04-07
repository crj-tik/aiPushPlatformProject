package com.tik.aipushcommon.feign.archive.dto;

import lombok.Data;

@Data
public class PersonQueryRequest {

    private String name;
    private String idCard;
    private String department;
    private String position;
}
