package org.example.springtask.dto;

import lombok.Data;
import org.example.springtask.entity.Worker;

import java.util.List;

@Data
public class ProjectDto {
    private Integer id;
    private String projectName;
    private List<Worker> workersId;
    private List<TaskDto> tasks;
}
