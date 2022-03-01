package org.example.springtask.repository.implementations;

import org.example.springtask.dto.Status;
import org.example.springtask.entity.File;
import org.example.springtask.entity.Task;
import org.example.springtask.repository.interfaces.FileDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@Repository(value = "fileDaoImpl")
public class FileDaoImpl implements FileDAO {


    public static final String FILE_PATH_STORED = "Путь к файлу записан в Базу Данных";
    public static final String NO_FILE_WITH_THIS_ID = "Файла с таким ID нет в базе данных.";

    private EntityManagerFactory entityManagerFactory;


    @Autowired
    public FileDaoImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    private Status getStatus(String text) {
        Status status = new Status();
        status.setStatus(text);
        return status;
    }

    @Override
    public Status createFile(Integer taskId, String pathToFile) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        Task task = em.find(Task.class, taskId);
        File file = new File();
        file.setId(task.getId());
        file.setPathToFile(pathToFile);
        file.setTask(task);
        em.persist(file);
        em.getTransaction().commit();
        em.close();
        return getStatus(FILE_PATH_STORED);
    }

    @Override
    public Status deleteFile(Integer taskId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        String name;
        try {
            File file = em.find(File.class, taskId);
            name = file.getPathToFile();
            em.remove(file);
        } catch (Exception e) {
            return getStatus(NO_FILE_WITH_THIS_ID);
        }
        em.getTransaction().commit();
        em.close();
        return getStatus(name);
    }

    @Override
    public Status getFilePath(Integer id) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        String name;
        try {
            File file = em.find(File.class, id);
            name = file.getPathToFile();
        } catch (Exception e) {
            return getStatus(NO_FILE_WITH_THIS_ID);
        }
        em.getTransaction().commit();
        em.close();
        return getStatus(name);
    }
}
