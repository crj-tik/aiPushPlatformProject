package com.tik.aipushcommon.feign.archive.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PersonResponse {

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
