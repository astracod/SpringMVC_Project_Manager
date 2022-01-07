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
        return "adminPages/viewShowAll/allWorkers";
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
// иметь 2 проета с задачами , где эти задачи объеденены одним сотрудником

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

    /**
     * используется в Authentication Controller при регитсрации пользователя
     * из ProjectService
     * здесь оставлен как пример метода для работы через Postman
     *
     * @param firstName
     * @param lastName
     * @param login
     * @param password
     * @return
     */
    @PutMapping("saveWorker")
    public Status saveWorker(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String login, @RequestParam String password) {
        return projectService.saveWorker(firstName, lastName, login, password);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("removeWorker")
    public String removeWorker(@RequestParam(value = "workerId") Integer workerId, Model model) {
        Status status = projectService.removeWorker(workerId);
        model.addAttribute("status", status.getStatus());
        return "adminPages/functionalWork/adminWorkOnProject";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("showProjects")
    public String getAllProjects(Model model) {
        List<OnlyProjectInfoDto> listP = projectService.showAllProjects();
        model.addAttribute("listP", listP);
        return "adminPages/viewShowAll/allProjects";
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
        return "adminPages/functionalWork/adminWorkOnProject";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("removeProject")
    public String removeProject(@RequestParam(value = "projectId") Integer projectId, Model model) {
        Status status = projectService.removeProject(projectId);
        model.addAttribute("status", status.getStatus());
        return "adminPages/functionalWork/adminWorkOnProject";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("addExecutor")
    public String addProjectExecutor(@RequestParam(value = "projectId") Integer projectId,
                                     @RequestParam(value = "workerId") Integer workerId,
                                     Model model) {
        Status status = projectService.addProjectExecutor(projectId, workerId);
        model.addAttribute("status", status.getStatus());
        return "adminPages/functionalWork/adminWorkOnProject";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("updateNameProject")
    public String changeProjectName(@RequestParam("projectId") Integer projectId,
                                    @RequestParam("newNameProject") String newNameProject,
                                    Model model) {
        Status status = projectService.changeProjectName(projectId, newNameProject);
        model.addAttribute("status", status.getStatus());
        return "adminPages/functionalWork/adminWorkOnProject";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("removeWorkerFromProject")
    public String removeWorkerFromProject(@RequestParam("projectId") Integer projectId,
                                          @RequestParam("workerId") Integer workerId,
                                          Model model) {
        Status status = projectService.removeWorkerFromProject(projectId, workerId);
        model.addAttribute("status", status.getStatus());
        return "adminPages/functionalWork/adminWorkOnProject";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("showTasks")
    public String getAllTasks(Model model) {
        List<TaskDto> tasks = projectService.getAllTasks();
        model.addAttribute("tasks", tasks);
        return "adminPages/viewShowAll/allTasks";
    }

    @PostMapping("showTask")
    public TaskDto getTask(Integer taskId) {
        return projectService.getTask(taskId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("createTask")
    public String createTask(@RequestParam(value = "taskName") String taskName,
                             @RequestParam(value = "projectId") Integer projectId,
                             @RequestParam(value = "textTask") String textTask,
                             Model model) {
        Status status = projectService.createTask(textTask, taskName, projectId);
        model.addAttribute("status", status.getStatus());
        return "adminPages/functionalWork/adminWorkOnTask";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("removeTask")
    public String removeTask(@RequestParam(value = "taskId") Integer taskId, Model model) {
        Status status = projectService.removeTask(taskId);
        model.addAttribute("status", status.getStatus());
        return "adminPages/functionalWork/adminWorkOnTask";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("assignExecutor")
    public String assignAnExecutorToTask(@RequestParam(value = "taskId") Integer taskId,
                                         @RequestParam(value = "workerId") Integer workerId,
                                         Model model) {
        Status status = projectService.assignAnExecutorToTask(taskId, workerId);
        model.addAttribute("status", status.getStatus());
        return "adminPages/functionalWork/adminWorkOnTask";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("removeExecutor")
    public String removeExecutorFromTask(@RequestParam(value = "taskId") Integer taskId,
                                         @RequestParam(value = "workerId") Integer workerId,
                                         Model model) {
        Status status = projectService.removeExecutorFromTask(taskId, workerId);
        model.addAttribute("status", status.getStatus());
        return "adminPages/functionalWork/adminWorkOnTask";
    }
}




























