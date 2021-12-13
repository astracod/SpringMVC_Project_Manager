package org.example.springtask.repository;

import org.example.springtask.dto.Status;
import org.example.springtask.entity.Project;
import org.example.springtask.entity.Task;
import org.example.springtask.entity.Worker;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjectDAO {
    List allWorkers();

    Worker getWorkerByEmail(String email);

    Worker getWorker(Integer workerId);

    Worker getAllInfoByWorkerId(Integer workerId);

    Status createWorker(String firstName, String lastName, String login, String password);

    Status removeWorker(Integer workerId);

    List allProjects();

    Project getAllInfoByProjectId(Integer projectId);

    List<Task> getAllProjectTasksByWorkerId(Integer workerId);

    List<Task> getAllProjectTasksByProjectId(Integer projectId);

    Project getProject(Integer projectId);

    Status createProject(String nameProject);

    Status removeProject(Integer projectId);

    Status changeProjectName(Integer projectId, String newNameProject);

    Status addProjectExecutor(Integer projectId, Integer workerId);

    Status assignAnExecutorToTask(Integer taskId, Integer workerId);

    Status removeExecutorFromTask(Integer taskId, Integer workerId);

    Status removeWorkerFromProject(Integer projectId, Integer workerId);

    List<Task> getAllTasks();

    Task getTask(Integer taskId);

    Status createTask(String taskName, LocalDateTime dateCreateTask, Integer project);

    Status removeTask(Integer taskId);

}
