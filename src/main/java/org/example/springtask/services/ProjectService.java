package org.example.springtask.services;

import lombok.extern.slf4j.Slf4j;
import org.example.springtask.dto.*;
import org.example.springtask.entity.Project;
import org.example.springtask.entity.Task;
import org.example.springtask.entity.Worker;
import org.example.springtask.exception.RequestProcessingException;
import org.example.springtask.mapping.ProjectMapping;
import org.example.springtask.repository.FileRepository;
import org.example.springtask.repository.ProjectDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service(value = "projectService")
public class ProjectService {

    private ProjectDAO projectDao;
    private FileRepository fileRepository;
    private ProjectMapping mapping;
    private PasswordEncoder passwordEncoder;
    private Clock clock;

    @Autowired
    public ProjectService(ProjectDAO projectDao, FileRepository fileRepository, ProjectMapping mapping, PasswordEncoder passwordEncoder, Clock clock) {
        this.projectDao = projectDao;
        this.fileRepository = fileRepository;
        this.mapping = mapping;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

/*    @Autowired
    public ProjectService(ProjectDAO projectDao, FileRepository fileRepository, ProjectMapping mapping, PasswordEncoder passwordEncoder) {
        this.projectDao = projectDao;
        this.fileRepository = fileRepository;
        this.mapping = mapping;
        this.passwordEncoder = passwordEncoder;

    }*/


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
        List<Task> tasks = projectDao.returnSheetTask(workerId);

        Set<String> statuses = new HashSet<>();

        if (!tasks.isEmpty()) {
            tasks.forEach(task -> {
                statuses.add(projectDao.removeExecutorFromTask(task.getId(), workerId).getStatus());
            });
        }

        statuses.add(projectDao.removeWorker(workerId).getStatus());

        Status status = new Status();
        StringBuilder answer = new StringBuilder();
        statuses.forEach(status1 -> {
                    answer.append(status1).append('\u0020');
                }
        );
        status.setStatus(answer.toString());

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
        Status removeProject = projectDao.removeProject(projectId);
        Status removeTask = new Status();
        StringBuilder stringBuilder = new StringBuilder();

        if (removeProject.getStatus().equals("Проект удален из базы данных.")) {
            for (Integer id : taskId) {
                stringBuilder.append(projectDao.removeTask(id).getStatus() + "\n");
                fileRepository.deleteFileTask(projectDao.deleteFile(id).getStatus());
            }
        }

        removeTask.setStatus(removeProject.getStatus() + " , " + stringBuilder.toString());

        return removeTask;
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
        return mapping.toTaskDtoList(projectDao.getAllTasks());
    }

    public TaskDto getTask(Integer taskId) {
        return mapping.toTaskDto(projectDao.getTask(taskId));
    }

    @Transactional
    public Status createTask(String text, String taskName, Integer projectId) {
        Status statusTheEnd = new Status();

        LocalDateTime dateTime = LocalDateTime.now(clock);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String textTime = dateTime.format(formatter);
        LocalDateTime parsedDate = LocalDateTime.parse(textTime, formatter);

        String answerDate = parsedDate.toString().substring(0, parsedDate.toString().indexOf("T"))
                .concat(" ")
                .concat(parsedDate.toString().substring(11, 19));

        Integer taskId = projectDao.getTaskByName(taskName);

        if (taskId > 0) {
            statusTheEnd.setStatus(refreshTaskInRemoteRepositoryAndDB(taskName, text, projectId, taskId, dateTime, answerDate));
        } else {
            statusTheEnd.setStatus(createTaskInRemoteRepositoryAndDB(text, taskName, projectId, dateTime, answerDate));
        }
        return statusTheEnd;
    }

    private String refreshTaskInRemoteRepositoryAndDB(String taskName, String text, Integer projectId, Integer taskId, LocalDateTime dateTime, String answerDate) {
        Status statusRefresh = projectDao.refreshTask(taskId, taskName, dateTime, projectId);
        fileRepository.giveTask(dateTime, text, taskName);
        String statusTask = statusRefresh.getStatus();
        return statusTask + " : " + answerDate;
    }

    private String createTaskInRemoteRepositoryAndDB(String text, String taskName, Integer projectId, LocalDateTime dateTime, String answerDate) {
        Status statusCreate = projectDao.createTask(taskName, dateTime, projectId);
        String statusTask = statusCreate.getStatus();
        Status statusRemote = fileRepository.giveTask(dateTime, text, taskName);
        String path = statusRemote.getAuxiliaryField().values().stream().findFirst().get();
        Integer taskIdForCreateFile = projectDao.getTaskByName(taskName);

        Status createFile = projectDao.createFile(taskIdForCreateFile, path);
        return statusRemote.getStatus() + " ; " + statusTask + " ; " + createFile.getStatus() + " : " + answerDate;
    }

    public Status removeTask(Integer taskId) {

        Status deleteTask = projectDao.removeTask(taskId);
        Status deleteFileTask = projectDao.deleteFile(taskId);

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
        return projectDao.removeExecutorFromTask(taskId, workerId);
    }


    @Transactional(readOnly = true)
    public ProjectDto getAllExecutorProjectsByProjectId(Integer projectId) {
        ProjectDto projectDto;
        try {
            projectDto = mapping.toProject(projectDao.getAllInfoByProjectId(projectId));
            projectDto.setTasks(mapping.toTaskDtoList(projectDao.getAllProjectTasksByProjectId(projectId)));
            projectDto.getTasks().forEach(
                    taskDto -> taskDto.setTextTask(fileRepository.getFileTaskByFileId(projectDao.getFilePath(taskDto.getId()).getStatus()))
            );
        } catch (Exception e) {
            throw new RequestProcessingException(" ВНИМАНИЕ!!!  Проекта с таким ID исполнителя нет в базе данных", e);
        }
        return projectDto;
    }

    @Transactional(readOnly = true)
    public FullWorkerDto getAllInfoByWorkerId(Integer workerId) {
        FullWorkerDto workerDto = new FullWorkerDto();

        try {
            workerDto.setWorker(mapping.toFullWorker(projectDao.getAllInfoByWorkerId(workerId)));
            workerDto.setTasks(mapping.toTaskDtoList(projectDao.getAllProjectTasksByWorkerId(workerId)));
        } catch (Exception e) {
            throw new RequestProcessingException(" ВНИМАНИЕ!!!  Исполнителя с таким ID нет в базе данных", e);
        }

        return workerDto;
    }
}
































