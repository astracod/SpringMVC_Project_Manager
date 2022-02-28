package org.example.springtask.mapping;

import lombok.extern.slf4j.Slf4j;
import org.example.springtask.dto.*;
import org.example.springtask.entity.Project;
import org.example.springtask.entity.Task;
import org.example.springtask.entity.Worker;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ProjectMapping {
    public WorkerDto toWorker(Worker worker) {
        WorkerDto workerDto = new WorkerDto();
        workerDto.setId(worker.getId());
        workerDto.setFirstName(worker.getFirstName());
        workerDto.setLastName(worker.getLastName());
        workerDto.setEmail(worker.getLogin());
        return workerDto;
    }

    public SecurityWorkerDto toSecurityWorker(Worker worker) {
        SecurityWorkerDto workerDto = new SecurityWorkerDto();
        workerDto.setId(worker.getId());
        workerDto.setFirstName(worker.getFirstName());
        workerDto.setLastName(worker.getLastName());
        workerDto.setUsername(worker.getLogin());
        workerDto.setPassword(worker.getPassword());
        return workerDto;
    }

    public List<WorkerDto> toWorkersDto(List<Worker> workers) {
        List<WorkerDto> workerDtoList = new ArrayList<>();
        for (Worker worker : workers) {
            workerDtoList.add(toWorker(worker));
        }
        return workerDtoList;
    }

    public WorkerWithProjectsDto toFullWorker(Worker worker) {
        WorkerWithProjectsDto workerDto = new WorkerWithProjectsDto();
        workerDto.setId(worker.getId());
        workerDto.setFirstName(worker.getFirstName());
        workerDto.setLastName(worker.getLastName());
        workerDto.setProjects(toProjectsInfoForWorkerDto(new ArrayList<>(worker.getProjects())));
        return workerDto;
    }

    private ProjectInfoForWorkerDto toProjectInfoForWorkerDto(Project projects) {
        ProjectInfoForWorkerDto piw = new ProjectInfoForWorkerDto();
        piw.setId(projects.getId());
        piw.setProjectName(projects.getProjectName());
        return piw;
    }

    public List<ProjectInfoForWorkerDto> toProjectsInfoForWorkerDto(List<Project> projects) {
        List<ProjectInfoForWorkerDto> projectDtoList = new ArrayList<>();
        for (Project project : projects) {
            projectDtoList.add(toProjectInfoForWorkerDto(project));
        }
        return projectDtoList;
    }

    public ProjectDto toProject(Project project) {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(project.getId());
        projectDto.setProjectName(project.getProjectName());
        projectDto.setWorkersId(project.getWorkers() == null ? new ArrayList<>() : new ArrayList<>(project.getWorkers()));
        return projectDto;
    }

    public OnlyProjectInfoDto toOnlyProjectInfoDto(Project project) {
        OnlyProjectInfoDto projectDto = new OnlyProjectInfoDto();
        projectDto.setId(project.getId());
        projectDto.setProjectName(project.getProjectName());
        return projectDto;
    }

    public List<OnlyProjectInfoDto> toOnlyProjectsInfoDto(List<Project> projects) {
        List<OnlyProjectInfoDto> projectDtoList = new ArrayList<>();
        for (Project project : projects) {
            projectDtoList.add(toOnlyProjectInfoDto(project));
        }
        return projectDtoList;
    }

    public TaskDto toTask(Task task) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        TaskDto taskDto = new TaskDto();
        taskDto.setId(task.getId());
        taskDto.setTaskName(task.getTaskName());
        taskDto.setDateCreateTask(task.getDateCreateTask().format(formatter));
        taskDto.setUserId(task.getUserId());
        if (task.getProject() != null) {
            taskDto.setProjectId(task.getProject().getId());
        }
        return taskDto;
    }

    public List<TaskDto> toDtoList(List<Task> tasks) {
        List<TaskDto> taskDtoList = new ArrayList<>();
        for (Task task : tasks) {
            taskDtoList.add(toTask(task));
        }
        return taskDtoList;
    }
}
