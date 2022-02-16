package org.example.springtask.repository.implementations;


import lombok.extern.slf4j.Slf4j;
import org.example.springtask.dto.Status;
import org.example.springtask.entity.Project;
import org.example.springtask.entity.Task;
import org.example.springtask.entity.Worker;
import org.example.springtask.exception.RequestProcessingException;
import org.example.springtask.repository.interfaces.ProjectDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository(value = "projectDaoImpl")
public class ProjectDaoImpl implements ProjectDAO {

    private EntityManagerFactory entityManagerFactory;

    @Autowired
    public ProjectDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    private Status getStatus(String text) {
        Status status = new Status();
        status.setStatus(text);
        return status;
    }

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
        Project project;
        try {
            project = em.createQuery("select p from Project p left join fetch p.workers pw where p.id =:projectId", Project.class)
                    .setParameter("projectId", projectId)
                    .getSingleResult();
        } catch (Exception e) {
            throw new RequestProcessingException(" ВНИМАНИЕ!!!  Проекта с таким ID исполнителя нет в базе данных" + projectId);
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
            throw new RequestProcessingException(" ВНИМАНИЕ!!!  Проекта с таким ID нет в базе данных. " + projectId);
        }
        em.getTransaction().commit();
        em.close();
        return project;
    }

    @Override
    public Project getProjectForDeleteTask(Integer projectId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        Project project = em.createQuery("select p from Project p join fetch p.tasks where p.id =:projectId", Project.class)
                .setParameter("projectId", projectId)
                .getSingleResult();

        if (project == null) {
            throw new RequestProcessingException(" ВНИМАНИЕ!!!  Проекта с таким ID нет в базе данных" + projectId);
        }
        em.getTransaction().commit();
        em.close();
        return project;
    }

    @Override
    public Status createProject(String nameProject) {
        if (nameProject.isEmpty()) {
            return getStatus("Задайте название проекту.");
        }
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
            return getStatus(" Проекта с таким ID нет в базе данных. " + projectId);
        }
        List<Task> tasks = new ArrayList<>(project.getTasks());
        for (Task task : tasks) {
            project.removeTask(task);
        }

        em.remove(project);
        em.getTransaction().commit();
        em.close();
        return getStatus("Проект удален из базы данных.");
    }


    @Override
    public Status changeProjectName(Integer projectId, String newNameProject) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        Project project = em.find(Project.class, projectId);
        if (project == null) {
            return getStatus(" Проекта с таким ID нет в базе данных. " + projectId);
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
}