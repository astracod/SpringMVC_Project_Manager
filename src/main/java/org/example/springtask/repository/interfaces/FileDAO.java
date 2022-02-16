package org.example.springtask.repository.interfaces;

import org.example.springtask.dto.Status;

public interface FileDAO {

    Status createFile(Integer taskId, String pathToFile);

    Status deleteFile(Integer taskId);

    Status getFilePath(Integer id);
}