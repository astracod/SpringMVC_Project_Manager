package org.example.springtask.repository.implementations;

import org.example.springtask.dto.Status;
import org.example.springtask.entity.Project;
import org.example.springtask.entity.Role;
import org.example.springtask.entity.Worker;
import org.example.springtask.exception.RequestProcessingException;
import org.example.springtask.repository.interfaces.WorkerDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

@Repository(value = "workerDaoImpl")
public class WorkerDaoImpl implements WorkerDAO {

    public static final String USER_WITH_THIS_LOGIN_IS_NOT_IN_THE_DATABASE = " Внимание!!! Пользователя с таким логином нет в базе данных.";
    public static final String USER_WITH_THIS_ID_IS_NOT_IN_THE_DATABASE = " Внимание!!! Пользователя с таким ID нет в базе данных.";
    public static final String USER_ADDED_TO_DATABASE = " Пользователь добавлен в базу данных.";
    public static final String USER_REMOVED_FROM_DATABASE = " Пользователь удален из базы данных.";

    private EntityManagerFactory entityManagerFactory;


    @Autowired
    public WorkerDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }


    private Status getStatus(String text) {
        Status status = new Status();
        status.setStatus(text);
        return status;
    }

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
        Worker worker;
        try {
            worker = em.createQuery("select w from Worker w where w.login = :email", Worker.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception e) {
            throw new UsernameNotFoundException(USER_WITH_THIS_LOGIN_IS_NOT_IN_THE_DATABASE + email);
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
            throw new RequestProcessingException(USER_WITH_THIS_ID_IS_NOT_IN_THE_DATABASE + workerId);
        }
        em.getTransaction().commit();
        em.close();
        return worker;
    }

    @Override
    public Worker getAllInfoByWorkerId(Integer workerId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        Worker worker;
        em.getTransaction().begin();
        try {
            worker = em.createQuery("select w from Worker w left join fetch w.projects wp where w.id = :workerId", Worker.class)
                    .setParameter("workerId", workerId)
                    .getSingleResult();
        } catch (Exception e) {
            throw new RequestProcessingException(USER_WITH_THIS_ID_IS_NOT_IN_THE_DATABASE + workerId);
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
        w.setRole(Role.USER);
        em.persist(w);
        em.getTransaction().commit();
        em.close();
        return getStatus(USER_ADDED_TO_DATABASE);
    }

    @Override
    public Status removeWorker(Integer workerId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        Worker worker = em.find(Worker.class, workerId);

        if (worker == null) {
            throw new RequestProcessingException(USER_WITH_THIS_ID_IS_NOT_IN_THE_DATABASE + workerId);
        }

        List<Project> projects = new ArrayList<>(worker.getProjects());

        for (Project project : projects) {
            worker.removeProject(project);
        }
        em.remove(worker);
        em.getTransaction().commit();
        em.close();
        return getStatus(USER_REMOVED_FROM_DATABASE);
    }
}
