package org.example.springtask.mapping;

import org.example.springtask.dto.FullWorkerDto;
import org.example.springtask.dto.ProjectInfoForWorkerDto;
import org.example.springtask.dto.TaskDto;
import org.example.springtask.dto.WorkerWithProjectsDto;
import org.example.springtask.entity.Project;
import org.example.springtask.entity.Task;
import org.example.springtask.entity.Worker;
import org.example.springtask.repository.ProjectDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProjectMappingTest {

    private ProjectMapping projectMapping;

    private LocalDateTime dateTime = LocalDateTime.now();

    @Mock
    private ProjectDAO projectDAO;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
        projectMapping = new ProjectMapping();
    }

    @Test
    void getAllInfoAboutWorkerDependencyCheck() {

        Integer workerId = 1;
        // Входные данные
        Project project = new Project();
        project.setId(workerId);
        project.setProjectName("Test Project");

        Set<Project> projects = new HashSet<>();
        projects.add(project);

        Worker worker = new Worker();
        worker.setId(workerId);
        worker.setFirstName("User");
        worker.setLastName("Userov");
        worker.setProjects(projects);

        Task task = new Task();
        task.setId(10);
        task.setProject(project);
        task.setTaskName("Test Task");

        task.setDateCreateTask(dateTime);
        task.setUserId(workerId);

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);

        List<TaskDto> taskDtos = projectMapping.toDtoList(tasks);

        FullWorkerDto workerDto = new FullWorkerDto();
        workerDto.setWorker(projectMapping.toFullWorker(worker));

        workerDto.setTasks(taskDtos);

        // ожидаемый результат
        ProjectInfoForWorkerDto projectInfoForWorkerDto = new ProjectInfoForWorkerDto();
        projectInfoForWorkerDto.setId(1);
        projectInfoForWorkerDto.setProjectName("Test Project");

        TaskDto taskDto = new TaskDto();
        taskDto.setId(10);
        taskDto.setTaskName("Test Task");
        taskDto.setDateCreateTask(dateTime.toString());
        taskDto.setUserId(1);
        taskDto.setProjectId(1);

        WorkerWithProjectsDto workerWithProjectsDto = new WorkerWithProjectsDto();
        workerWithProjectsDto.setId(1);
        workerWithProjectsDto.setFirstName("User");
        workerWithProjectsDto.setLastName("Userov");
        workerWithProjectsDto.setProjects(List.of(projectInfoForWorkerDto));

        List<TaskDto> taskDtoList = new ArrayList<>();
        taskDtoList.add(taskDto);


        Mockito.when(projectDAO.getAllInfoByWorkerId(1))
                .thenReturn(worker);


        Mockito.when(projectDAO.getAllProjectTasksByWorkerId(1))
                .thenReturn(tasks);


        WorkerWithProjectsDto withProjectsDtoCheck = projectMapping.toFullWorker(projectDAO.getAllInfoByWorkerId(1));

        List<TaskDto> tasksDtoCheck = projectMapping.toDtoList(projectDAO.getAllProjectTasksByWorkerId(1));

        Assertions.assertEquals(workerWithProjectsDto, withProjectsDtoCheck);
        Assertions.assertEquals(taskDtos, tasksDtoCheck);
    }
}
