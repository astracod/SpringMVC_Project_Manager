package org.example.springtask.repository.interfaces;

import org.example.springtask.dto.Status;
import org.example.springtask.entity.Task;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskDAO {
    List<Task> getAllTasks();

    Task getTask(Integer taskId);

    Integer getTaskByName(String taskName);

    Status createTask(String taskName, LocalDateTime dateCreateTask, Integer project);

    Status refreshTask(Integer Id, String taskName, LocalDateTime dateCreateTask, Integer project);

    Status removeTask(Integer taskId);

    List<Task> returnSheetTask(Integer workerId);

    Status assignAnExecutorToTask(Integer taskId, Integer workerId);

    Status removeExecutorFromTask(Integer taskId, Integer workerId);
}
