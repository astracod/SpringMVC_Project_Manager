package org.example.springtask.dto;

import lombok.Data;

@Data
public class SecurityWorkerDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
