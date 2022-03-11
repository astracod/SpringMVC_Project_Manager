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

    public static final String NO_SUCH_TASKS_IN_THE_DATABASE = " Внимание!!! В базе данных нет задач.";
    public static final String NO_TASKS_WITH_THIS_ID = " Внимание!!! Задачи с таким ID  нет в базе данных. ";
    public static final String TASK_CREATED_IN_THE_DATABASE = "Задача создана в базе данных.";
    public static final String TASK_TEXT_UPDATED = "Текст задачи обновлен в удаленном хранилище.";
    public static final String TASK_REMOVED_FROM_DATABASE = "Задача удалена из базы данных : ";
    public static final String EMPLOYEE_WITH_SUCH_ID_IS_NOT_IN_THE_DATABASE = " ВНИМАНИЕ!!!  Исполнителя с таким ID нет в базе данных. ";
    public static final String EXECUTOR_IS_ASSIGNED_TO_TASK = " Исполнитель присвоен задаче";
    public static final String EXECUTOR_REMOVED_FROM_TASK = " Исполнитель удален из задачи";
    public static final String SYMBOL_POINT = " .";
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
            throw new RequestProcessingException(NO_SUCH_TASKS_IN_THE_DATABASE);
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
            throw new RequestProcessingException(NO_TASKS_WITH_THIS_ID + taskId);
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

        return getStatus(TASK_CREATED_IN_THE_DATABASE);
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
        return getStatus(TASK_TEXT_UPDATED);
    }

    @Override
    public Status removeTask(Integer taskId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        Task task = em.find(Task.class, taskId);

        if (task == null) {
            return getStatus(NO_TASKS_WITH_THIS_ID + taskId);
        }
        em.remove(task);
        em.getTransaction().commit();
        em.close();
        return getStatus(TASK_REMOVED_FROM_DATABASE + taskId + SYMBOL_POINT);
    }

    @Override
    public Status assignAnExecutorToTask(Integer taskId, Integer workerId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();

        Worker worker = em.find(Worker.class, workerId);
        if (worker == null) {
            return getStatus(EMPLOYEE_WITH_SUCH_ID_IS_NOT_IN_THE_DATABASE + workerId);
        }

        Task task = em.find(Task.class, taskId);
        if (task == null) {
            throw new RequestProcessingException(NO_TASKS_WITH_THIS_ID + taskId);
        }

        task.setUserId(workerId);
        em.persist(task);
        em.getTransaction().commit();
        em.close();

        return getStatus(EXECUTOR_IS_ASSIGNED_TO_TASK);
    }

    @Override
    public Status removeExecutorFromTask(Integer taskId, Integer workerId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();

        Worker worker = em.find(Worker.class, workerId);
        if (worker == null) {
            return getStatus(EMPLOYEE_WITH_SUCH_ID_IS_NOT_IN_THE_DATABASE + workerId);
        }

        Task task = em.find(Task.class, taskId);
        if (task == null) {
            throw new RequestProcessingException(NO_TASKS_WITH_THIS_ID + taskId);
        }

        task.setUserId(null);
        em.persist(task);
        em.getTransaction().commit();
        em.close();

        return getStatus(EXECUTOR_REMOVED_FROM_TASK);
    }

    public List<Task> returnSheetTask(Integer workerId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();

        Worker worker = em.find(Worker.class, workerId);
        if (worker == null) {
            throw new RequestProcessingException(EMPLOYEE_WITH_SUCH_ID_IS_NOT_IN_THE_DATABASE + workerId);
        }
        List<Task> task = em.createQuery("select t from Task t where t.userId =:workerId")
                .setParameter("workerId", workerId)
                .getResultList();

        em.getTransaction().commit();
        em.close();

        return task;
    }
}
