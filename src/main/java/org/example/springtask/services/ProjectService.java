package org.example.springtask.services;

import lombok.extern.slf4j.Slf4j;
import org.example.springtask.dto.*;
import org.example.springtask.entity.Project;
import org.example.springtask.entity.Worker;
import org.example.springtask.mapping.ProjectMapping;
import org.example.springtask.repository.ProjectDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service(value = "projectService")
public class ProjectService {

    private ProjectDAO projectDao;
    private ProjectMapping mapping;
    private PasswordEncoder passwordEncoder;


    @Autowired
    public ProjectService(ProjectDAO projectDao, ProjectMapping mapping, PasswordEncoder passwordEncoder) {
        this.projectDao = projectDao;
        this.mapping = mapping;
        this.passwordEncoder = passwordEncoder;
    }


    public List<WorkerDto> showAllUsers() {
        List workers = projectDao.allWorkers();
        return mapping.toWorkersDto(workers);
    }

    public Status saveWorker(String firstName, String lastName, String login, String password) {
        Status status = new Status();
        if (firstName == null || lastName == null || login == null || password == null){
            status.setStatus("Заполните все требуемые поля данных пользователя при регистрации.");
        }else {
            String codPassword = passwordEncoder.encode(password);
            status = projectDao.createWorker(firstName, lastName, login, codPassword);
        }
        return  status;
    }

    public Status removeWorker(Integer workerId) {
        return projectDao.removeWorker(workerId);
    }

    /**
     * метод используется для аутентификации пользователя в классе AuthProviderImpl
     *
     * @param email
     * @return
     */
    @Transactional(readOnly = true)
    public SecurityWorkerDto getWorkerByEmail(String email) {
        Worker worker = projectDao.getWorkerByEmail(email);
        return mapping.toSecurityWorker(worker);
    }

    public WorkerDto getWorker(Integer workerId) {
        return mapping.toWorker(projectDao.getWorker(workerId));
    }


    public List<OnlyProjectInfoDto> showAllProjects() {
        return mapping.toOnlyProjectsInfoDto(projectDao.allProjects());
    }

    @Transactional(readOnly = true)
    public OnlyProjectInfoDto getOnlyProjectInfo(Integer projectId) {
        return mapping.toOnlyProjectInfoDto(projectDao.getProject(projectId));
    }

    public Status createProject(String nameProject) {
        return projectDao.createProject(nameProject);
    }

    public Status removeProject(Integer projectId) {
        return projectDao.removeProject(projectId);
    }

    public Status addProjectExecutor(Integer projectId, Integer workerId) {
        return projectDao.addProjectExecutor(projectId, workerId);
    }

    public Status changeProjectName(Integer projectId, String newNameProject) {
        return projectDao.changeProjectName(projectId, newNameProject);
    }

    public Status removeWorkerFromProject(Integer projectId, Integer workerId) {
        return projectDao.removeWorkerFromProject(projectId, workerId);
    }


    public List getAllTasks() {
        return mapping.toDtoList(projectDao.getAllTasks());
    }

    public TaskDto getTask(Integer taskId) {
        return mapping.toTask(projectDao.getTask(taskId));
    }

    public Status createTask(String taskName, Integer projectId) {
        LocalDateTime dateTime = LocalDateTime.now();
        return projectDao.createTask(taskName, dateTime, projectId);
    }

    public Status removeTask(Integer taskId) {
        return projectDao.removeTask(taskId);
    }

    public Status assignAnExecutorToTask(Integer taskId, Integer workerId) {
        return projectDao.assignAnExecutorToTask(taskId, workerId);
    }

    public Status removeExecutorFromTask(Integer taskId, Integer workerId) {
        return projectDao.removeExecutorFromTask(taskId, workerId);
    }

    @Transactional(readOnly = true)
    public ProjectDto getAllExecutorProjectsByProjectId(Integer projectId) {

        ProjectDto projectDto;
        Project project = projectDao.getAllInfoByProjectId(projectId);
        projectDto = mapping.toProject(project);
        projectDto.setTasks(mapping.toDtoList(projectDao.getAllProjectTasksByProjectId(projectId)));
        return projectDto;
    }

    @Transactional(readOnly = true)
    public FullWorkerDto getAllInfoByWorkerId(Integer workerId) {
        FullWorkerDto workerDto = new FullWorkerDto();
        Worker worker = projectDao.getAllInfoByWorkerId(workerId);
        workerDto.setWorker(mapping.toFullWorker(worker));
        workerDto.setTasks(mapping.toDtoList(projectDao.getAllProjectTasksByWorkerId(workerId)));
        return workerDto;
    }
}
































