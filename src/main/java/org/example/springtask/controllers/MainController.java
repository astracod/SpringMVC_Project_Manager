package org.example.springtask.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springtask.dto.*;
import org.example.springtask.entity.Task;
import org.example.springtask.services.ProjectService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MainController {

    private final ProjectService projectService;


    @GetMapping("/")
    public String test() {
        return "You are logged in ";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("showWorkers")
    public List getAllWorkers() {
        return projectService.showAllUsers();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("showWorker")
    public WorkerDto getWorker(Integer workerId) {
        return projectService.getWorker(workerId);
    }

    @PostMapping("showWorkerById")
    public FullWorkerDto getAllInfoByWorkerId(Integer workerId) {
        return projectService.getAllInfoByWorkerId(workerId);
    }

    @PutMapping("saveWorker")
    public Status saveWorker(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String login, @RequestParam String password) {
        return projectService.saveWorker(firstName, lastName, login, password);
    }

    @DeleteMapping("removeWorker")
    public Status removeWorker(@RequestParam Integer workerId) {
        return projectService.removeWorker(workerId);
    }

    @GetMapping("showProjects")
    public List<OnlyProjectInfoDto> getAllProjects() {
        return projectService.showAllProjects();
    }

    @GetMapping("getById")
    public ProjectDto allProjects(Integer projectId) {
        return projectService.getAllExecutorProjectsByProjectId(projectId);
    }

    @GetMapping("showProject")
    public OnlyProjectInfoDto getOnlyProjectInfo(Integer projectId) {
        return projectService.getOnlyProjectInfo(projectId);
    }

    @PutMapping("createProject")
    public Status changeProject(String nameProject) {
        return projectService.createProject(nameProject);
    }

    @DeleteMapping("removeProject")
    public Status removeProject(Integer projectId) {
        return projectService.removeProject(projectId);
    }

    @PostMapping("addExecutor")
    public Status addProjectExecutor(Integer projectId, Integer workerId) {
        return projectService.addProjectExecutor(projectId, workerId);
    }

    @PostMapping("updateNameProject")
    public Status changeProjectName(Integer projectId, String newNameProject) {
        return projectService.changeProjectName(projectId, newNameProject);
    }

    @DeleteMapping("removeWorkerFromProject")
    public Status removeWorkerFromProject(Integer projectId, Integer workerId) {
        return projectService.removeWorkerFromProject(projectId, workerId);
    }

    @GetMapping("showTasks")
    public List<Task> getAllTasks() {
        return projectService.getAllTasks();
    }

    @PostMapping("showTask")
    public TaskDto getTask(Integer taskId) {
        return projectService.getTask(taskId);
    }

    @PutMapping("createTask")
    public Status createTask(String taskName, Integer projectId) {
        return projectService.createTask(taskName, projectId);
    }

    @DeleteMapping("removeTask")
    public Status removeTask(Integer taskId) {
        return projectService.removeTask(taskId);
    }

    @PostMapping("assignExecutor")
    public Status assignAnExecutorToTask(Integer taskId, Integer workerId) {
        return projectService.assignAnExecutorToTask(taskId, workerId);
    }

    @PostMapping("removeExecutor")
    public Status removeExecutorFromTask(Integer taskId, Integer workerId) {
        return projectService.removeExecutorFromTask(taskId, workerId);
    }
}




























