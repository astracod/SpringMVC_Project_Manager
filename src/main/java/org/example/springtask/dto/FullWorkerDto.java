package org.example.springtask.dto;

import lombok.Data;

import java.util.List;

@Data
public class FullWorkerDto {

    private WorkerWithProjectsDto worker;

    private List<TaskDto> tasks;


}
