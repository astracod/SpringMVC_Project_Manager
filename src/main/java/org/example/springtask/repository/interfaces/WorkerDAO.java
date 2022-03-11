package org.example.springtask.repository.interfaces;

import org.example.springtask.dto.Status;
import org.example.springtask.entity.Worker;

import java.util.List;

public interface WorkerDAO {
    List allWorkers();

    Worker getWorkerByEmail(String email);

    Worker getWorker(Integer workerId);

    Worker getAllInfoByWorkerId(Integer workerId);

    Status createWorker(String firstName, String lastName, String login, String password);

    Status removeWorker(Integer workerId);
}
