package com.tik.aipusharchiveservice.service.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PersonEntity {

    private Long id;
    private String name;
    private String idCard;
    private String department;
    private String position;
    private String phone;
    private String email;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
