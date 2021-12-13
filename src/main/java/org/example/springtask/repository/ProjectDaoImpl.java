package org.example.springtask.repository;


import lombok.extern.slf4j.Slf4j;
import org.example.springtask.dto.Status;
import org.example.springtask.entity.Project;
import org.example.springtask.entity.Task;
import org.example.springtask.entity.Worker;
import org.example.springtask.exception.RequestProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
public class ProjectDaoImpl implements ProjectDAO {


    private EntityManagerFactory entityManagerFactory;


    @Autowired
    public ProjectDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }


    /**
     * Методы работы с классом Worker
     *
     * @return
     */
    @Override
    public List allWorkers() {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        List workers = em.createQuery("from Worker").getResultList();
        em.getTransaction().commit();
        em.close();
        return workers;
    }

    @Override
    public Worker getWorkerByEmail(String email) {
        EntityManager em = entityManagerFactory.createEntityManager();
        Worker worker ;
        try {
            worker = em.createQuery("select w from Worker w where w.login = :email", Worker.class)
                    .setParameter("email", email)
                    .getSingleResult();
        }catch (Exception e){
            throw new UsernameNotFoundException(" Внимание!!! Пользователя с таким логином нет в базе данных.");
        }
        em.close();
        return worker;
    }

    @Override
    public Worker getWorker(Integer workerId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        Worker worker = em.find(Worker.class, workerId);
        if (worker == null) {
            throw new RequestProcessingException(" ВНИМАНИЕ!!!  Исполнителя с таким ID нет в базе данных");
        }
        em.getTransaction().commit();
        em.close();
        return worker;
    }

    @Override
    public Worker getAllInfoByWorkerId(Integer workerId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        Worker worker = em.createQuery("select w from Worker w left join fetch w.projects wp where w.id = :workerId", Worker.class)
                .setParameter("workerId", workerId)
                .getSingleResult();
        if (worker == null) {
            throw new RequestProcessingException(" ВНИМАНИЕ!!!  Исполнителя с таким ID нет в базе данных");
        }
        em.getTransaction().commit();
        em.close();
        return worker;
    }

    @Override
    public Status createWorker(String firstName, String lastName, String login, String codPassword) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        Worker w = new Worker();
        w.setFirstName(firstName);
        w.setLastName(lastName);
        w.setLogin(login);
        w.setPassword(codPassword);
        em.persist(w);
        em.getTransaction().commit();
        em.close();
        return getStatus(" Исполнитель добавлен в базу данных.");
    }

    private Status getStatus(String text) {
        Status status = new Status();
        status.setStatus(text);
        return status;
    }

    @Override
    public Status removeWorker(Integer workerId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        Worker worker = em.find(Worker.class, workerId);
        if (worker == null) {
            throw new RequestProcessingException(" ВНИМАНИЕ!!!  Исполнителя с таким ID нет в базе данных");
        }
        em.remove(worker);
        em.getTransaction().commit();
        em.close();
        return getStatus(" Исполнитель удален из базы данных.");
    }

    /**
     * Методы работы с классом Project
     *
     * @return
     */
    @Override
    public List<Project> allProjects() {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        TypedQuery<Project> query = em.createQuery("select p from Project p", Project.class);
        List<Project> projects = query.getResultList();
        em.getTransaction().commit();
        em.close();
        return projects;
    }

    @Override
    public Project getAllInfoByProjectId(Integer projectId) {

        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        Project project = new Project();
        try {
            project = em.createQuery("select p from Project p left join fetch p.workers pw where p.id =:projectId", Project.class)
                    .setParameter("projectId", projectId)
                    .getSingleResult();
        } catch (Exception e) {
            throw new RequestProcessingException(" ВНИМАНИЕ!!!  Проекта с таким ID исполнителя нет в базе данных");
        }


        em.getTransaction().commit();
        em.close();
        return project;
    }

    /**
     * получение всех задач по ID исполнителя
     *
     * @param workerId
     * @return
     */
    public List getAllProjectTasksByWorkerId(Integer workerId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();

        List tasks = em.createQuery("select t from Task t left join fetch t.project  where t.userId = :workerId", Task.class)
                .setParameter("workerId", workerId)
                .getResultList();

        em.getTransaction().commit();
        em.close();

        return tasks;
    }

    @Override
    public List<Task> getAllProjectTasksByProjectId(Integer projectId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();

        List tasks = em.createQuery("select t from Task t left join fetch t.project tp where tp.id = :projectId", Task.class)
                .setParameter("projectId", projectId)
                .getResultList();

        em.getTransaction().commit();
        em.close();

        return tasks;
    }


    @Override
    public Project getProject(Integer projectId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        Project project = em.find(Project.class, projectId);
        if (project == null) {
            throw new RequestProcessingException(" ВНИМАНИЕ!!!  Проекта с таким ID нет в базе данных");
        }
        em.getTransaction().commit();
        em.close();
        return project;
    }

    @Override
    public Status createProject(String nameProject) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        Project project = new Project();
        project.setProjectName(nameProject);
        em.persist(project);
        em.getTransaction().commit();
        em.close();
        return getStatus(" Проект создан.");
    }

    @Override
    public Status removeProject(Integer projectId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        Project project = em.find(Project.class, projectId);
        if (project == null) {
            return getStatus(" Проекта с таким ID нет в базе данных.");
        }
        em.remove(project);
        em.getTransaction().commit();
        em.close();
        return getStatus(" Проект удален из базы данных.");
    }

    @Override
    public Status changeProjectName(Integer projectId, String newNameProject) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        Project project = em.find(Project.class, projectId);
        if (project == null) {
            return getStatus(" Проекта с таким ID нет в базе данных.");
        }
        project.setProjectName(newNameProject);
        em.persist(project);
        em.getTransaction().commit();
        em.close();
        return getStatus("Имя проекта успешно заменено");
    }

    @Override
    public Status addProjectExecutor(Integer projectId, Integer workerId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();

        /**
         *  проверка входящего ID проекта на наличие в базе данных
         */
        Project project = em.find(Project.class, projectId);
        if (project == null) {
            return getStatus(" Проекта с таким ID нет в базе данных.");
        }

        /**
         * проверка входящего ID исполнителя на наличие в базе данных
         */
        Worker worker = em.find(Worker.class, workerId);
        if (worker == null) {
            return getStatus(" ВНИМАНИЕ!!!  Исполнителя с таким ID нет в базе данных");
        }

        /**
         *  Создание нового объекта исполнителя, запись его в объект проекта и передача данных в базу
         */
        project.addWorker(worker);
        em.persist(project);

        em.getTransaction().commit();
        em.close();
        return getStatus(" Данные исполнителя изменены");
    }

    @Override
    public Status assignAnExecutorToTask(Integer taskId, Integer workerId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();

        Worker worker = em.find(Worker.class, workerId);
        if (worker == null) {
            return getStatus(" ВНИМАНИЕ!!!  Исполнителя с таким ID нет в базе данных");
        }

        Task task = em.find(Task.class, taskId);
        if (task == null) {
            throw new RequestProcessingException(" Внимание!!! Задачи с таким ID нет в базе данных.");
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
            return getStatus(" ВНИМАНИЕ!!!  Исполнителя с таким ID нет в базе данных");
        }

        Task task = em.find(Task.class, taskId);
        if (task == null) {
            throw new RequestProcessingException(" Внимание!!! Задачи с таким ID нет в базе данных.");
        }

        task.setUserId(null);
        em.persist(task);
        em.getTransaction().commit();
        em.close();

        return getStatus(" Исполнитель удален из задачи");
    }


    @Override
    public Status removeWorkerFromProject(Integer projectId, Integer workerId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();

        Project project = em.find(Project.class, projectId);
        if (project == null) {
            return getStatus("Проекта с заданными условиями нет в базе");
        }
        Worker worker = em.find(Worker.class, workerId);
        if (worker == null) {
            return getStatus(" ВНИМАНИЕ!!!  Исполнителя с таким ID нет в базе данных");
        }

        project.removeWorker(worker);
        em.persist(project);

        em.getTransaction().commit();
        em.close();
        return getStatus("Исполнитель удален из проекта.");
    }

    /**
     *  Методы работы с классом Task
     * @return
     */
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
            throw new RequestProcessingException(" Внимание!!! Задачи с таким ID нет в базе данных.");
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
        try {
            em.persist(task);
        } catch (Exception exception) {
            return getStatus(" Внимание!!! Проекта с таким ID нет в базе данных.");
        }
        em.getTransaction().commit();
        em.close();
        return getStatus("Задача создана в базе данных.");
    }

    @Override
    public Status removeTask(Integer taskId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        Task task = em.find(Task.class, taskId);
        if (task == null) {
            return getStatus("Задачи с таким ID нет в базе данных.");
        }
        em.remove(task);
        em.getTransaction().commit();
        em.close();
        return getStatus("Задача удалена из базы данных.");
    }
}
