package org.example.springtask.repository.implementations;

import org.example.springtask.dto.Status;
import org.example.springtask.entity.Project;
import org.example.springtask.entity.Task;
import org.example.springtask.entity.Worker;
import org.example.springtask.exception.RequestProcessingException;
import org.example.springtask.repository.interfaces.TaskDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.List;

@Repository(value = "taskDaoImpl")
public class TaskDaoImpl implements TaskDAO {

    private EntityManagerFactory entityManagerFactory;


    @Autowired
    public TaskDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    private Status getStatus(String text) {
        Status status = new Status();
        status.setStatus(text);
        return status;
    }

    @Override
    public List getAllTasks() {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        List tasks = em.createQuery("from Task").getResultList();
        if (tasks == null) {
            throw new RequestProcessingException(" Внимание!!! В базе данных нет задач.");
        }
        em.getTransaction().commit();
        em.close();
        return tasks;
    }

    @Override
    public Task getTask(Integer taskId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        Task task = em.find(Task.class, taskId);
        if (task == null) {
            throw new RequestProcessingException(" Внимание!!! Задачи с таким ID  нет в базе данных. " + taskId);
        }
        em.getTransaction().commit();
        em.close();
        return task;
    }

    @Override
    public Integer getTaskByName(String taskName) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        Integer task;
        try {
            task = em.createQuery("select t from Task t where t.taskName =:taskName", Task.class)
                    .setParameter("taskName", taskName)
                    .getSingleResult().getId();
        } catch (Exception e) {
            task = -1;
        }

        em.getTransaction().commit();
        em.close();

        return task;
    }

    @Override
    public Status createTask(String taskName, LocalDateTime dateCreateTask, Integer projectID) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();

        Project project1 = new Project();
        project1.setId(projectID);
        Task task = new Task();
        task.setTaskName(taskName);
        task.setDateCreateTask(dateCreateTask);
        task.setProject(project1);
        em.persist(task);
        em.getTransaction().commit();
        em.close();

        return getStatus("Задача создана в базе данных.");
    }

    @Override
    public Status refreshTask(Integer taskId, String taskName, LocalDateTime dateCreateTask, Integer projectID) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();

        Task task = em.find(Task.class, taskId);

        task.setDateCreateTask(dateCreateTask);
        em.persist(task);
        em.getTransaction().commit();
        em.close();
        return getStatus("Текст задачи обновлен в удаленном хранилище.");
    }

    @Override
    public Status removeTask(Integer taskId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        Task task = em.find(Task.class, taskId);

        if (task == null) {
            return getStatus("Задачи с таким ID  нет в базе данных. " + taskId);
        }
        em.remove(task);
        em.getTransaction().commit();
        em.close();
        return getStatus("Задача удалена из базы данных : " + taskId + " .");
    }

    @Override
    public Status assignAnExecutorToTask(Integer taskId, Integer workerId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();

        Worker worker = em.find(Worker.class, workerId);
        if (worker == null) {
            return getStatus(" ВНИМАНИЕ!!!  Исполнителя с таким ID нет в базе данных. " + workerId);
        }

        Task task = em.find(Task.class, taskId);
        if (task == null) {
            throw new RequestProcessingException(" Внимание!!! Задачи с таким ID нет в базе данных. " + taskId);
        }

        task.setUserId(workerId);
        em.persist(task);
        em.getTransaction().commit();
        em.close();

        return getStatus(" Исполнитель присвоен задаче");
    }

    @Override
    public Status removeExecutorFromTask(Integer taskId, Integer workerId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();

        Worker worker = em.find(Worker.class, workerId);
        if (worker == null) {
            return getStatus(" ВНИМАНИЕ!!!  Исполнителя с таким ID нет в базе данных " + workerId);
        }

        Task task = em.find(Task.class, taskId);
        if (task == null) {
            throw new RequestProcessingException(" Внимание!!! Задачи с таким ID нет в базе данных. " + taskId);
        }

        task.setUserId(null);
        em.persist(task);
        em.getTransaction().commit();
        em.close();

        return getStatus(" Исполнитель удален из задачи");
    }

    public List<Task> returnSheetTask(Integer workerId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();

        Worker worker = em.find(Worker.class, workerId);
        if (worker == null) {
            throw new RequestProcessingException(" Внимание!!! Сотрудника с таким ID нет в базе данных. "+ workerId);
        }
        List<Task> task = em.createQuery("select t from Task t where t.userId =:workerId")
                .setParameter("workerId", workerId)
                .getResultList();

        em.getTransaction().commit();
        em.close();

        return task;
    }
}
