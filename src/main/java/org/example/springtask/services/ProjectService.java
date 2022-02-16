package org.example.springtask.services;

import lombok.extern.slf4j.Slf4j;
import org.example.springtask.dto.*;
import org.example.springtask.entity.File;
import org.example.springtask.entity.Project;
import org.example.springtask.entity.Task;
import org.example.springtask.entity.Worker;
import org.example.springtask.mapping.ProjectMapping;
import org.example.springtask.repository.FileRepository;
import org.example.springtask.repository.ProjectDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service(value = "projectService")
public class ProjectService {

    private ProjectDAO projectDao;
    private FileRepository fileRepository;
    private ProjectMapping mapping;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public ProjectService(ProjectDAO projectDao, FileRepository fileRepository, ProjectMapping mapping, PasswordEncoder passwordEncoder) {
        this.projectDao = projectDao;
        this.fileRepository = fileRepository;
        this.mapping = mapping;
        this.passwordEncoder = passwordEncoder;
    }

    public List<WorkerDto> showAllUsers() {
        List workers = projectDao.allWorkers();
        return mapping.toWorkersDto(workers);
    }

    public Status saveWorker(String firstName, String lastName, String login, String password) {

        Status status = new Status();
        if (firstName == null || lastName == null || login == null || password == null) {
            status.setStatus("Заполните все требуемые поля данных пользователя при регистрации.");
        } else {
            String codPassword = passwordEncoder.encode(password);
            status = projectDao.createWorker(firstName, lastName, login, codPassword);
        }
        return status;
    }

    public Status removeWorker(Integer workerId) {
        Worker worker = projectDao.getWorker(workerId);

        List<Task> tasks = projectDao.returnSheetTask(workerId);

        Set<String> statuses = new HashSet<>();

        if (!tasks.isEmpty()) {
            tasks.forEach(task -> {
                statuses.add(projectDao.removeExecutorFromTask(task, worker).getStatus());
            });
        }

        statuses.add(projectDao.removeWorker(worker).getStatus());

        Status status = new Status();
        StringBuilder answer = new StringBuilder();
        statuses.forEach(status1 -> {
                    answer.append(status1).append(" ; ");
                }

        );

        return status;
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

    @Transactional
    public Status removeProject(Integer projectId) {
        Project project = projectDao.getProjectForDeleteTask(projectId);
        List<Integer> taskId = new ArrayList<>();
        for (Task task : project.getTasks()) {
            taskId.add(task.getId());
        }
        Status removeProject = projectDao.removeProject(project);
        Status removeTask = new Status();
        StringBuilder stringBuilder = new StringBuilder();

        if (removeProject.getStatus().equals("Проект удален из базы данных.")) {
            for (Integer id : taskId) {
                Task task = projectDao.getTask(id);
                File file = projectDao.getFile(id);
                stringBuilder.append(projectDao.removeTask(task).getStatus() + "\n");
                fileRepository.deleteFileTask(projectDao.deleteFile(file).getStatus());
            }
        }

        removeTask.setStatus(removeProject.getStatus() + " , " + stringBuilder.toString());

        return removeTask;
    }

    public Status addProjectExecutor(Integer projectId, Integer workerId) {
        Project project = projectDao.getProject(projectId);

        Worker worker = projectDao.getWorker(workerId);

        return projectDao.addProjectExecutor(project, worker);
    }

    public Status changeProjectName(Integer projectId, String newNameProject) {
        Project project = projectDao.getProject(projectId);
        return projectDao.changeProjectName(project, newNameProject);
    }

    public Status removeWorkerFromProject(Integer projectId, Integer workerId) {
        Project project = projectDao.getProject(projectId);
        Worker worker = projectDao.getWorker(workerId);
        return projectDao.removeWorkerFromProject(project, worker);
    }


    public List getAllTasks() {
        return mapping.toDtoList(projectDao.getAllTasks());
    }

    public TaskDto getTask(Integer taskId) {
        return mapping.toTask(projectDao.getTask(taskId));
    }

    @Transactional
    public Status createTask(String text, String taskName, Integer projectId) {
        Status statusRefresh;
        Status statusCreate;
        Status createFile = null;
        Status statusTheEnd = new Status();
        String statusTask;
        String path = "";
        Integer taskId;
        LocalDateTime dateTime = LocalDateTime.now();

        Status statusRemote = fileRepository.giveTask(dateTime, text, taskName);

        taskId = projectDao.getTaskByName(taskName);

        if (taskId > 0) {
            Task task = projectDao.getTask(taskId);
            statusRefresh = projectDao.refreshTask(task, dateTime);
            statusTask = statusRefresh.getStatus();
        } else {
            statusCreate = projectDao.createTask(taskName, dateTime, projectId);
            statusTask = statusCreate.getStatus();
            taskId = projectDao.getTaskByName(taskName);
            Optional<String> addressTask = statusRemote.getAuxiliaryField().values().stream().findFirst();
            if (addressTask.isPresent()) {
                path = addressTask.get();
            }
            Task task = projectDao.getTask(taskId);
            createFile = projectDao.createFile(task, path);
        }

        String answerDate = dateTime.toString().substring(0, dateTime.toString().indexOf("."));

        if (createFile != null) {
            statusTheEnd.setStatus(statusRemote.getStatus() + "\n" + statusTask + "\n" + createFile.getStatus() + answerDate);
        } else {
            statusTheEnd.setStatus("Данные задачи обновленны : " + answerDate);
        }
        return statusTheEnd;
    }

    public Status removeTask(Integer taskId) {
        Task task = projectDao.getTask(taskId);

        Status deleteTask = projectDao.removeTask(task);

        File file = projectDao.getFile(taskId);

        Status deleteFileTask = projectDao.deleteFile(file);

        boolean deleteRemoteStorage = fileRepository.deleteFileTask(deleteFileTask.getStatus());

        Status finishAnswer = new Status();
        if (deleteTask != null && deleteRemoteStorage) {
            finishAnswer.setStatus(" Задача : " + deleteFileTask.getStatus().substring(0, deleteFileTask.getStatus().indexOf(".")) + " удалена.");
        } else if (!deleteRemoteStorage) {
            finishAnswer.setStatus("Задача не удалена");
        }
        return finishAnswer;
    }

    public Status assignAnExecutorToTask(Integer taskId, Integer workerId) {
        return projectDao.assignAnExecutorToTask(taskId, workerId);
    }

    public Status removeExecutorFromTask(Integer taskId, Integer workerId) {
        Task task = projectDao.getTask(taskId);
        Worker worker = projectDao.getWorker(workerId);
        return projectDao.removeExecutorFromTask(task, worker);
    }

    @Transactional(readOnly = true)
    public ProjectDto getAllExecutorProjectsByProjectId(Integer projectId) {
        Project project = projectDao.getAllInfoByProjectId(projectId);
        ProjectDto projectDto = mapping.toProject(project);
        projectDto.setTasks(mapping.toDtoList(projectDao.getAllProjectTasksByProjectId(projectId)));
        projectDto.getTasks().forEach(
                taskDto -> taskDto.setTextTask(fileRepository.getFileTaskByFileId(projectDao.getFile(taskDto.getId()).getPathToFile()))
        );

        return projectDto;
    }

    @Transactional(readOnly = true)
    public FullWorkerDto getAllInfoByWorkerId(Integer workerId) {
        FullWorkerDto workerDto = new FullWorkerDto();
        Worker worker = projectDao.getWorker(workerId);
        workerDto.setWorker(mapping.toFullWorker(worker));
        workerDto.setTasks(mapping.toDtoList(projectDao.getAllProjectTasksByWorkerId(workerId)));
        return workerDto;
    }
}
































