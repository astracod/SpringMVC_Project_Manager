package org.example.springtask.mapping;

import org.example.springtask.dto.*;
import org.example.springtask.entity.Project;
import org.example.springtask.entity.Role;
import org.example.springtask.entity.Task;
import org.example.springtask.entity.Worker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectMappingTest {

    private static final String TASK_NAME = "Task Name";
    private static final LocalDateTime DATE_TIME = LocalDateTime.now();
    private ProjectMapping mapping = new ProjectMapping();

    private static final int WORKER_ID = 1;
    private static final String WORKER_FIRST_NAME = "User";
    private static final String WORKER_LAST_NAME = "Userov";
    private static final String LOGIN = "user@mail.ru";
    private static final String PASSWORD = "user";


    @Test
    @DisplayName("toWorkersDto")
    void shouldReturnListWorkerDto() {
        Worker worker = new Worker(
                WORKER_ID, WORKER_FIRST_NAME, WORKER_LAST_NAME, LOGIN, PASSWORD, Role.USER, Set.of()
        );
        List<Worker> workers = List.of(worker);

        WorkerDto expectedWorker = new WorkerDto();
        expectedWorker.setId(WORKER_ID);
        expectedWorker.setEmail(LOGIN);
        expectedWorker.setFirstName(WORKER_FIRST_NAME);
        expectedWorker.setLastName(WORKER_LAST_NAME);

        List<WorkerDto> result = mapping.toWorkersDto(workers);

        assertThat(result).containsExactly(expectedWorker);
    }


    @Test
    @DisplayName("toTaskDto")
    void shouldReturnTaskDto() {
        LocalDateTime timeNow = DATE_TIME;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Task task = new Task(WORKER_ID, TASK_NAME, timeNow, WORKER_ID, null);
        TaskDto expectedResult = new TaskDto();
        expectedResult.setId(WORKER_ID);
        expectedResult.setTaskName(TASK_NAME);
        expectedResult.setDateCreateTask(timeNow.format(formatter));
        expectedResult.setUserId(WORKER_ID);
        expectedResult.setProjectId(null);

        TaskDto result = mapping.toTaskDto(task);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("toFullWorker")
    void shouldReturnToWorkerWithProjectsDto() {
        Project projectForWorker = new Project();
        projectForWorker.setId(WORKER_ID);
        projectForWorker.setProjectName(TASK_NAME);

        Worker worker = new Worker(
                WORKER_ID, WORKER_FIRST_NAME, WORKER_LAST_NAME, LOGIN, PASSWORD, Role.USER, Set.of(projectForWorker)
        );

        ProjectInfoForWorkerDto expectedProject = new ProjectInfoForWorkerDto();
        expectedProject.setId(WORKER_ID);
        expectedProject.setProjectName(TASK_NAME);

        WorkerWithProjectsDto expectedWorkerDto = new WorkerWithProjectsDto();
        expectedWorkerDto.setId(WORKER_ID);
        expectedWorkerDto.setFirstName(WORKER_FIRST_NAME);
        expectedWorkerDto.setLastName(WORKER_LAST_NAME);
        expectedWorkerDto.setProjects(List.of(expectedProject));

        WorkerWithProjectsDto result = mapping.toFullWorker(worker);

        assertThat(result).isEqualTo(expectedWorkerDto);
    }

    @Test
    @DisplayName("toProject")
    void shouldReturnProjectDto() {
        Project projectForMethod = new Project();
        projectForMethod.setId(WORKER_ID);
        projectForMethod.setProjectName(TASK_NAME);
        projectForMethod.setTasks(null);
        projectForMethod.setWorkers(Set.of());

        ProjectDto exceptedDto = new ProjectDto();
        exceptedDto.setId(WORKER_ID);
        exceptedDto.setProjectName(TASK_NAME);
        exceptedDto.setTasks(null);
        exceptedDto.setWorkersId(List.of());

        ProjectDto result = mapping.toProject(projectForMethod);
        assertThat(result).isEqualTo(exceptedDto);
    }

    @Test
    @DisplayName("toTaskDtoList")
    void shouldReturnTaskDtoList() {
        LocalDateTime timeNow = DATE_TIME;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Task taskForMethod = new Task(WORKER_ID, TASK_NAME, timeNow, WORKER_ID, null);

        TaskDto taskDto = new TaskDto();
        taskDto.setId(WORKER_ID);
        taskDto.setTaskName(TASK_NAME);
        taskDto.setDateCreateTask(timeNow.format(formatter));
        taskDto.setUserId(WORKER_ID);
        taskDto.setProjectId(null);

        List<TaskDto> expectedTaskDtoList = List.of(taskDto);

        List<TaskDto> result = mapping.toTaskDtoList(List.of(taskForMethod));

        assertThat(result).isEqualTo(expectedTaskDtoList);
    }

    @Test
    @DisplayName("toOnlyProjectInfoDto")
    void shouldReturnOnlyProjectInfoDtoObject() {
        OnlyProjectInfoDto expected = new OnlyProjectInfoDto();
        expected.setId(WORKER_ID);
        expected.setProjectName(TASK_NAME);

        Project projectForToOnlyProjectInfoDtoMethod = new Project();
        projectForToOnlyProjectInfoDtoMethod.setId(WORKER_ID);
        projectForToOnlyProjectInfoDtoMethod.setProjectName(TASK_NAME);

        OnlyProjectInfoDto result = mapping.toOnlyProjectInfoDto(projectForToOnlyProjectInfoDtoMethod);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("toOnlyProjectsInfoDto")
    void shouldReturnOnlyProjectInfoDtoList() {
        OnlyProjectInfoDto expected = new OnlyProjectInfoDto();
        expected.setId(WORKER_ID);
        expected.setProjectName(TASK_NAME);
        List<OnlyProjectInfoDto> expectedList = List.of(expected);

        Project projectForToOnlyProjectsInfoDtoMethod = new Project();
        projectForToOnlyProjectsInfoDtoMethod.setId(WORKER_ID);
        projectForToOnlyProjectsInfoDtoMethod.setProjectName(TASK_NAME);
        List<Project> projectListForToOnlyProjectsInfoDtoMethod = List.of(projectForToOnlyProjectsInfoDtoMethod);

        List<OnlyProjectInfoDto> result = mapping.toOnlyProjectsInfoDto(projectListForToOnlyProjectsInfoDtoMethod);

        assertThat(result).isEqualTo(expectedList);
    }
}


















