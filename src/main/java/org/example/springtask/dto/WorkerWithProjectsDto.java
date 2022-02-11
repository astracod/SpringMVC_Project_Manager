package org.example.springtask.dto;

import lombok.Data;

import java.util.List;

@Data
public class WorkerWithProjectsDto {

    private Integer id;

    private String firstName;

    private String lastName;

    private List<OnlyProjectInfoDto> projects;
}
