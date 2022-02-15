package org.example.springtask.repository;

import org.example.springtask.dto.Status;
import org.example.springtask.entity.File;
import org.example.springtask.entity.Project;
import org.example.springtask.entity.Task;
import org.example.springtask.entity.Worker;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjectDAO {
    List allWorkers();

    Worker getWorkerByEmail(String email);

    Worker getWorker(Integer workerId);

    Status createWorker(String firstName, String lastName, String login, String password);

    Status removeWorker(Worker worker);

    List allProjects();

    Project getAllInfoByProjectId(Integer projectId);

    List<Task> getAllProjectTasksByWorkerId(Integer workerId);

    List<Task> getAllProjectTasksByProjectId(Integer projectId);

    Project getProject(Integer projectId);

    Project getProjectForDeleteTask(Integer projectId);

    Status createProject(String nameProject);

    Status removeProject(Project project);

    Status changeProjectName(Project project, String newNameProject);

    Status getStatus(String text);

    Status addProjectExecutor(Project project, Worker worker);

    Status assignAnExecutorToTask(Integer taskId, Integer workerId);

    Status removeExecutorFromTask(Task task, Worker worker);

    Status removeWorkerFromProject(Project project, Worker worker);

    List<Task> getAllTasks();

    Task getTask(Integer taskId);

    Integer getTaskByName(String taskName);

    Status createTask(String taskName, LocalDateTime dateCreateTask, Integer project);

    Status refreshTask(Task task,  LocalDateTime dateCreateTask);

    Status removeTask(Task task);

    List<Task> returnSheetTask(Integer workerId);

    Status createFile(Task task, String pathToFile);

    File getFile(Integer id);

    Status deleteFile(File file);

}
