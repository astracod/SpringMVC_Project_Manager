package org.example.springtask.dto;

import lombok.Data;

@Data
public class TaskDto {

    private Integer id;

    private String taskName;

    private String dateCreateTask;

    private Integer projectId;

    private Integer userId;

    private String textTask;

}
