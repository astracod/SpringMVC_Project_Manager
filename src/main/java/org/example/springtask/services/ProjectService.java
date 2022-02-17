package org.example.springtask.services;

import lombok.extern.slf4j.Slf4j;
import org.example.springtask.dto.*;
import org.example.springtask.entity.Project;
import org.example.springtask.entity.Task;
import org.example.springtask.entity.Worker;
import org.example.springtask.mapping.ProjectMapping;
import org.example.springtask.repository.FileRepository;
import org.example.springtask.repository.interfaces.FileDAO;
import org.example.springtask.repository.interfaces.ProjectDAO;
import org.example.springtask.repository.interfaces.TaskDAO;
import org.example.springtask.repository.interfaces.WorkerDAO;
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
    private WorkerDAO workerDao;
    private TaskDAO taskDao;
    private FileDAO fileDao;
    private FileRepository fileRepository;
    private ProjectMapping mapping;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public ProjectService(ProjectDAO projectDao, WorkerDAO workerDao, TaskDAO taskDao, FileDAO fileDao, FileRepository fileRepository, ProjectMapping mapping, PasswordEncoder passwordEncoder) {
        this.projectDao = projectDao;
        this.workerDao = workerDao;
        this.taskDao = taskDao;
        this.fileDao = fileDao;
        this.fileRepository = fileRepository;
        this.mapping = mapping;
        this.passwordEncoder = passwordEncoder;
    }

    public List<WorkerDto> showAllUsers() {
        List workers = workerDao.allWorkers();
        return mapping.toWorkersDto(workers);
    }

    public Status saveWorker(String firstName, String lastName, String login, String password) {

        Status status = new Status();
        if (firstName == null || lastName == null || login == null || password == null) {
            status.setStatus("Заполните все требуемые поля данных пользователя при регистрации.");
        } else {
            String codPassword = passwordEncoder.encode(password);
            status = workerDao.createWorker(firstName, lastName, login, codPassword);
        }
        return status;
    }

    public Status removeWorker(Integer workerId) {
        List<Task> tasks = taskDao.returnSheetTask(workerId);

        Set<String> statuses = new HashSet<>();

        if (!tasks.isEmpty()) {
            tasks.forEach(task -> {
                statuses.add(taskDao.removeExecutorFromTask(task.getId(), workerId).getStatus());
            });
        }

        statuses.add(workerDao.removeWorker(workerId).getStatus());

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
        Worker worker = workerDao.getWorkerByEmail(email);
        return mapping.toSecurityWorker(worker);
    }

    public WorkerDto getWorker(Integer workerId) {
        return mapping.toWorker(workerDao.getWorker(workerId));
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
                stringBuilder.append(taskDao.removeTask(id).getStatus() + "\n");
                fileRepository.deleteFileTask(fileDao.deleteFile(id).getStatus());
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
        return mapping.toDtoList(taskDao.getAllTasks());
    }

    public TaskDto getTask(Integer taskId) {
        return mapping.toTask(taskDao.getTask(taskId));
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

        taskId = taskDao.getTaskByName(taskName);

        if (taskId > 0) {
            statusRefresh = taskDao.refreshTask(taskId, taskName, dateTime, projectId);
            statusTask = statusRefresh.getStatus();
        } else {
            statusCreate = taskDao.createTask(taskName, dateTime, projectId);
            statusTask = statusCreate.getStatus();
            taskId = taskDao.getTaskByName(taskName);
            Optional<String> addressTask = statusRemote.getAuxiliaryField().values().stream().findFirst();
            if (addressTask.isPresent()) {
                path = addressTask.get();
            }
            createFile = fileDao.createFile(taskId, path);
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

        Status deleteTask = taskDao.removeTask(taskId);
        Status deleteFileTask = fileDao.deleteFile(taskId);

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
        return taskDao.assignAnExecutorToTask(taskId, workerId);
    }

    public Status removeExecutorFromTask(Integer taskId, Integer workerId) {
        return taskDao.removeExecutorFromTask(taskId, workerId);
    }

    @Transactional(readOnly = true)
    public ProjectDto getAllExecutorProjectsByProjectId(Integer projectId) {

        List<Integer> taskList = new ArrayList<>();
        Map<String, String> taskMap = new HashMap<>();
        Map<String, String> textTask = new HashMap<>();
        ProjectDto projectDto;
        Project project = projectDao.getAllInfoByProjectId(projectId);
        projectDto = mapping.toProject(project);
        projectDto.setTasks(mapping.toDtoList(projectDao.getAllProjectTasksByProjectId(projectId)));

        for (TaskDto task : projectDto.getTasks()) {
            taskList.add(task.getId());
        }
        for (Integer integer : taskList) {
            taskMap.put(String.valueOf(integer), fileDao.getFilePath(integer).getStatus());
        }
        for (Map.Entry<String, String> stringStringEntry : taskMap.entrySet()) {
            if (!stringStringEntry.getValue().equals("Файла с таким ID нет в базе данных.")) {
                log.info("СЕРВИС : {}", stringStringEntry.getKey());
                textTask.put(stringStringEntry.getKey(),
                        fileRepository.getFileTaskByFileId(stringStringEntry.getValue()));
            }
        }
        for (Map.Entry<String, String> stringStringEntry : textTask.entrySet()) {

            for (TaskDto task : projectDto.getTasks()) {
                if (task.getId() == Integer.valueOf(stringStringEntry.getKey())) {
                    if (stringStringEntry.getValue() != null) {
                        task.setTextTask(stringStringEntry.getValue());
                    }
                }
            }
        }

        return projectDto;
    }

    @Transactional(readOnly = true)
    public FullWorkerDto getAllInfoByWorkerId(Integer workerId) {
        FullWorkerDto workerDto = new FullWorkerDto();
        Worker worker = workerDao.getAllInfoByWorkerId(workerId);
        workerDto.setWorker(mapping.toFullWorker(worker));
        workerDto.setTasks(mapping.toDtoList(projectDao.getAllProjectTasksByWorkerId(workerId)));
        return workerDto;
    }
}
