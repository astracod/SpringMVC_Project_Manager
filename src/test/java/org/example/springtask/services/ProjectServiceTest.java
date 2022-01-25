package org.example.springtask.services;

import org.example.springtask.dto.FullWorkerDto;
import org.example.springtask.entity.Project;
import org.example.springtask.entity.Task;
import org.example.springtask.entity.Worker;
import org.example.springtask.mapping.ProjectMapping;
import org.example.springtask.repository.FileRepository;
import org.example.springtask.repository.ProjectDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class ProjectServiceTest {


    @Mock
    private FileRepository fileRepository;

    @Mock
    PasswordEncoder encoder;

    @Mock
    private ProjectMapping projectMapping;

    @Mock
    private ProjectDAO projectDAO;

    private ProjectService projectService;

    private LocalDateTime dateTime = LocalDateTime.now();


    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
        projectService = new ProjectService(projectDAO, fileRepository, projectMapping, encoder);
    }


    @Test
    void getAllInfoAboutWorkerServiceCheck() {
        // Входные данные
        Integer workerId = 1;
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

        FullWorkerDto objectToCompare = new FullWorkerDto();
        objectToCompare.setWorker(projectMapping.toFullWorker(worker));
        objectToCompare.setTasks(projectMapping.toDtoList(tasks));

        // запрос
        FullWorkerDto dto = projectService.getAllInfoByWorkerId(1);

        Assertions.assertNotNull(dto);
        Assertions.assertEquals(objectToCompare, dto);
    }

}
