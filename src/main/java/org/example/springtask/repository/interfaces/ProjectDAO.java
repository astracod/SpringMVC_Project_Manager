package org.example.springtask.repository.interfaces;

import org.example.springtask.dto.Status;
import org.example.springtask.entity.Project;
import org.example.springtask.entity.Task;

import java.util.List;

public interface ProjectDAO {

    List allProjects();

    Project getAllInfoByProjectId(Integer projectId);

    List<Task> getAllProjectTasksByWorkerId(Integer workerId);

    List<Task> getAllProjectTasksByProjectId(Integer projectId);

    Project getProject(Integer projectId);

    Project getProjectForDeleteTask(Integer projectId);

    Status createProject(String nameProject);

    Status removeProject(Integer projectId);

    Status changeProjectName(Integer projectId, String newNameProject);

    Status addProjectExecutor(Integer projectId, Integer workerId);

    Status removeWorkerFromProject(Integer projectId, Integer workerId);
}
