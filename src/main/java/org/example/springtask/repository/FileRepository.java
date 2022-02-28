package org.example.springtask.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springtask.dto.Status;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component(value = "fileRepository")
@RequiredArgsConstructor
public class FileRepository {

    public static final String TASK_IS_FORMED_ON_REMOTE_RESOURCE = "Задача сформирована на удаленом ресурсе";
    public static final String ERROR_IN_TRANSFER_OF_INFORMATION_TO_REMOTE_RESOURCE = "Ошибка передачи задачи в хранилище данных";
    public static final String FILE_EXTENSION = ".txt";
    public static final String SPACE = " ";
    public static final String UNDERSCORE = "_";
    public static final String DOUBLE_BACKSLASH = "\\";
    public static final String NEL = "\n";
    public static final String TASK_GET_ERROR = "Ошибка получения задачи с удаленного репозитория";
    public final String REMOTE_REPOSITORY = "C:\\Users\\Admin\\Desktop\\scp";

    public Status giveTask(LocalDateTime dateCreateTask, String text, String taskName) {

        String newText = dateCreateTask + NEL + text + NEL;
        byte[] textTask = newText.getBytes();
        Status status = new Status();
        File uploadDirectory = new File(REMOTE_REPOSITORY);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdir();
        }

        String fileName = taskName.replace(SPACE, UNDERSCORE) + FILE_EXTENSION;
        String lastPath = uploadDirectory + DOUBLE_BACKSLASH + fileName;
        File checkFile = new File(lastPath);
        if (!checkFile.isFile()) {
            try {
                checkFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File resultPath = new File(lastPath);
        FileOutputStream fos = null;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream(4096)) {
            if (!resultPath.exists()) {
                resultPath.createNewFile();
            }
            out.write(textTask);
            fos = new FileOutputStream(resultPath, true);
            out.writeTo(fos);
            out.flush();

        } catch (Exception e) {
            status.setStatus(ERROR_IN_TRANSFER_OF_INFORMATION_TO_REMOTE_RESOURCE + taskName);
            return status;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Map<String, String> fileNameFromTask = new HashMap<>();
        fileNameFromTask.put("fileName", fileName);
        status.setStatus(TASK_IS_FORMED_ON_REMOTE_RESOURCE);
        status.setAuxiliaryField(fileNameFromTask);
        return status;
    }

    public boolean deleteFileTask(String name) {

        String absolutePath = REMOTE_REPOSITORY + DOUBLE_BACKSLASH + name;
        Path path = Paths.get(absolutePath);

        boolean res = Files.exists(path);
        if (res) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                res = false;
            }
        }
        return res;
    }

    public String getFileTaskByFileId(String fileName) {
        String absolutePath = REMOTE_REPOSITORY + DOUBLE_BACKSLASH + fileName;
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        try (BufferedReader reader = new BufferedReader(new FileReader(absolutePath))) {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
            stringBuilder.append(TASK_GET_ERROR);
        }
        return stringBuilder.toString();
    }
}
