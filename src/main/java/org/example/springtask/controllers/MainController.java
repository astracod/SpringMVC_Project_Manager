package org.example.springtask.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springtask.dto.*;
import org.example.springtask.services.ProjectService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final ProjectService projectService;


    @GetMapping("/")
    public String sortByRole(Authentication auth) {
        String myRole = auth.getAuthorities().stream().findFirst().get().toString();
        if (myRole.equals("ADMIN")) {
            return "adminPages/adminShowObject";
        }
        return "userPages/user";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("showWorkers")
    public String getAllWorkers(Model model) {
        List<WorkerDto> listW = projectService.showAllUsers();
        model.addAttribute("listW", listW);
        return "adminPages/allWorkers";
    }

    /**
     * определить по роли
     *
     * @param workerId
     * @return
     */
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("showWorker")
    public WorkerDto getWorker(Integer workerId) {
        return projectService.getWorker(workerId);
    }

    /**
     * получить всю информацию где задействован сотрудник по его ID
     *
     * @param workerId
     * @param model
     * @return
     */
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("showWorkerById")
    public String getAllInfoByWorkerId(@RequestParam(value = "workerId") Integer workerId, Model model) {
        FullWorkerDto fullWorkerDto = projectService.getAllInfoByWorkerId(workerId);
        model.addAttribute("infoAboutUser", fullWorkerDto);
        return "userPages/userInfoByWorkerId";
    }


    /**
     * получить всю информацию о проекте по его ID (задачи, сотрудники)
     *
     * @param projectId
     * @param model
     * @return
     */
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("getById")
    public String allProjects(@RequestParam(value = "projectId") Integer projectId, Model model) {
        ProjectDto projectDto = projectService.getAllExecutorProjectsByProjectId(projectId);
        model.addAttribute("project", projectDto);
        return "userPages/infoProjectById";
    }

    @PutMapping("saveWorker")
    public Status saveWorker(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String login, @RequestParam String password) {
        return projectService.saveWorker(firstName, lastName, login, password);
    }

    @DeleteMapping("removeWorker")
    public Status removeWorker(@RequestParam Integer workerId) {
        return projectService.removeWorker(workerId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("showProjects")
    public String getAllProjects(Model model) {
        List<OnlyProjectInfoDto> listP = projectService.showAllProjects();
        model.addAttribute("listP", listP);
        return "adminPages/allProjects";
    }


    @GetMapping("showProject")
    public OnlyProjectInfoDto getOnlyProjectInfo(Integer projectId) {
        return projectService.getOnlyProjectInfo(projectId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("createProject")
    public String changeProject(@RequestParam(value = "nameProject") String nameProject, Model model) {
        Status status = projectService.createProject(nameProject);
        model.addAttribute("status", status.getStatus());
        return "adminPages/adminWorkOnObjects";
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("removeProject")
    public String removeProject(@RequestParam(value = "projectId")Integer projectId, Model model) {
        Status status = projectService.removeProject(projectId);
        model.addAttribute("status", status.getStatus());
        return "adminPages/adminWorkOnObjects";
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

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("showTasks")
    public String getAllTasks(Model model) {
        List<TaskDto> tasks = projectService.getAllTasks();
        model.addAttribute("tasks", tasks);
        return "adminPages/allTasks";
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




























