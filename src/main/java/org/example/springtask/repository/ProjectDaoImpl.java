package org.example.springtask.repository;


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


    public static final String NO_USER_WITH_THIS_LOGIN = " Внимание!!! Пользователя с таким логином нет в базе данных.";
    public static final String NO_USER_WITH_THIS_ID = " ВНИМАНИЕ!!!  Исполнителя с таким ID нет в базе данных";
    public static final String WORKER_ADDED_TO_THE_DB = " Исполнитель добавлен в базу данных.";
    public static final String WORKER_REMOVED_FROM_THE_DB = " Исполнитель удален из базы данных.";
    public static final String NO_PROJECT_WITH_THIS_ID = " ВНИМАНИЕ!!!  Проекта с таким ID нет в базе данных";
    public static final String GIVE_NAME_THE_PROJECT = "Задайте название проекту.";
    public static final String PROJECT_CREATE = " Проект создан.";
    public static final String PROJECT_REMOVED = "Проект удален из базы данных.";
    public static final String SUCCESSFUL_CHANGE_THE_NAME_OF_PROJECT = "Имя проекта успешно заменено";
    public static final String WORKER_DATA_SUCCESSFULLY_CHANGED = " Данные исполнителя изменены";
    public static final String NO_TASK_WITH_THIS_ID = " Внимание!!! Задачи с таким ID нет в базе данных.";
    public static final String WORKER_ASSIGNED_TO_TASK = " Исполнитель присвоен задаче";
    public static final String WORKER_REMOVED_FROM_TASK = " Исполнитель удален из задачи.";
    public static final String THE_PROJECT_WITH_THE_SPECIFIED_CONDITIONS_IS_NOT_IN_THE_DATABASE = "Проекта с заданными условиями нет в базе";
    public static final String WORKER_REMOVED_FROM_PROJECT = "Исполнитель удален из проекта.";
    public static final String DB_DOESNT_HAVE_TASKS = " Внимание!!! В базе данных нет задач.";
    public static final String TASK_CREATE = "Задача создана в базе данных.";
    public static final String TASK_TEXT_IS_UPDATED_IN_REMOTE_STORAGE = "Текст задачи обновлен в удаленном хранилище.";
    public static final String TASK_REMOVED_FROM_DB = "Задача удалена из базы данных : ";
    public static final String SYMBOL_POINT = " .";
    public static final String FILE_PATH_STORED = "Путь к файлу записан в Базу Данных";
    public static final String NO_FILE_WITH_THIS_ID = "Файла с таким ID нет в базе данных.";

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

        Project project = em.createQuery("select p from Project p left join fetch p.workers pw where p.id =:projectId", Project.class)
                .setParameter("projectId", projectId)
                .getSingleResult();


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
    public List<Task> getAllProjectTasksByWorkerId(Integer workerId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();

        List<Task> tasks = em.createQuery("select t from Task t left join fetch t.project  where t.userId = :workerId", Task.class)
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
            throw new RequestProcessingException(NO_PROJECT_WITH_THIS_ID);
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
            throw new RequestProcessingException(NO_PROJECT_WITH_THIS_ID);
        }
        em.getTransaction().commit();
        em.close();
        return project;
    }

    @Override
    public Status createProject(String nameProject) {
        if (nameProject.isEmpty()) {
            return getStatus(GIVE_NAME_THE_PROJECT);
        }
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        Project project = new Project();
        project.setProjectName(nameProject);
        em.persist(project);
        em.getTransaction().commit();
        em.close();
        return getStatus(PROJECT_CREATE);
    }

    @Override
    public Status removeProject(Integer projectId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        Project project = em.find(Project.class, projectId);
        if (project == null) {
            return getStatus(NO_PROJECT_WITH_THIS_ID);
        }
        List<Task> tasks = new ArrayList<>(project.getTasks());
        for (Task task : tasks) {
            project.removeTask(task);
        }

        em.remove(project);
        em.getTransaction().commit();
        em.close();
        return getStatus(PROJECT_REMOVED);
    }


    @Override
    public Status changeProjectName(Integer projectId, String newNameProject) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        Project project = em.find(Project.class, projectId);
        if (project == null) {
            return getStatus(NO_PROJECT_WITH_THIS_ID);
        }
        project.setProjectName(newNameProject);
        em.persist(project);
        em.getTransaction().commit();
        em.close();
        return getStatus(SUCCESSFUL_CHANGE_THE_NAME_OF_PROJECT);
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
            return getStatus(NO_PROJECT_WITH_THIS_ID);
        }

        /**
         * проверка входящего ID исполнителя на наличие в базе данных
         */
        Worker worker = em.find(Worker.class, workerId);
        if (worker == null) {
            return getStatus(NO_USER_WITH_THIS_ID);
        }

        /**
         *  Создание нового объекта исполнителя, запись его в объект проекта и передача данных в базу
         */
        project.addWorker(worker);
        em.persist(project);

        em.getTransaction().commit();
        em.close();
        return getStatus(WORKER_DATA_SUCCESSFULLY_CHANGED);
    }

/*    public List<Task> returnSheetTask(Integer workerId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();

        Worker worker = em.find(Worker.class, workerId);
        if (worker == null) {
            throw new RequestProcessingException(NO_USER_WITH_THIS_ID);
        }
        List<Task> task = em.createQuery("select t from Task t where t.userId =:workerId")
                .setParameter("workerId", workerId)
                .getResultList();

        em.getTransaction().commit();
        em.close();

        return task;
    }*/


    @Override
    public Status removeWorkerFromProject(Integer projectId, Integer workerId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();

        Project project = em.find(Project.class, projectId);

        if (project == null) {
            return getStatus(THE_PROJECT_WITH_THE_SPECIFIED_CONDITIONS_IS_NOT_IN_THE_DATABASE);
        }
        Worker worker = em.find(Worker.class, workerId);
        if (worker == null) {
            return getStatus(NO_USER_WITH_THIS_ID);
        }

        project.removeWorker(worker);
        em.persist(project);

        em.getTransaction().commit();
        em.close();
        return getStatus(WORKER_REMOVED_FROM_PROJECT);
    }
}
