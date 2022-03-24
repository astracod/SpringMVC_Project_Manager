package org.example.springtask.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springtask.dto.Status;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "fileRepository")
@RequiredArgsConstructor
public class FileRepository {

    public static final String TASK_IS_FORMED_ON_REMOTE_RESOURCE = "Задача сформирована на удаленом ресурсе";
    public static final String ERROR_IN_TRANSFER_OF_INFORMATION_TO_REMOTE_RESOURCE = "Ошибка передачи задачи в хранилище данных : ";
    public static final String FILE_EXTENSION = ".txt";
    public static final String SPACE = " ";
    public static final String UNDERSCORE = "_";
    public static final String DOUBLE_BACKSLASH = "\\";
    public static final String NEL = "\n";
    public static final String ERROR_CREATING_DIRECTORY = "Ошибка создания удаленной директории : ";
    public static final String ERROR_TO_RECEIVE_DATA_FROM_REMOTE_REPOSITORY = "Ошибка получения данных с удаленного репозитория : ";
    public static final String ERROR_CREATING_DIRECTORY_2 = " при создании задачи : ";
    public static final String ERROR_IN_TRANSFER_2 = " при записи в файл : ";
    public static final String ERROR_TO_RECEIVE_2 = " при чтении файла : ";
    public final String REMOTE_REPOSITORY = "C:\\Users\\Admin\\Desktop\\scp";

    public Status giveTask(LocalDateTime dateCreateTask, String text, String taskName) {

        String newText = dateCreateTask + NEL + text + NEL;

        Status status = new Status();

        String fileName = taskName.replace(SPACE, UNDERSCORE) + FILE_EXTENSION;
        String lastPath = REMOTE_REPOSITORY + DOUBLE_BACKSLASH + fileName;
        if (!Files.isDirectory(Paths.get(REMOTE_REPOSITORY))) {
            try {
                Files.createDirectories(Paths.get(REMOTE_REPOSITORY));
            } catch (IOException e) {
                status.setStatus(ERROR_CREATING_DIRECTORY + REMOTE_REPOSITORY + ERROR_CREATING_DIRECTORY_2 + taskName);
            }
        }
        try {
            Files.write(Paths.get(lastPath), newText.getBytes(), new StandardOpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND});
        } catch (IOException e) {
            status.setStatus(ERROR_IN_TRANSFER_OF_INFORMATION_TO_REMOTE_RESOURCE + REMOTE_REPOSITORY + ERROR_IN_TRANSFER_2 + taskName);
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
                return false;
            }
        }
        return res;
    }

    public String getFileTaskByFileId(String fileName) {
        String absolutePath = REMOTE_REPOSITORY + DOUBLE_BACKSLASH + fileName;
        StringBuilder stringBuilder = new StringBuilder();
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(absolutePath));
            for (String line : lines) {
                stringBuilder.append(line);
                stringBuilder.append(System.lineSeparator());
            }
        } catch (IOException e) {
            stringBuilder.append(ERROR_TO_RECEIVE_DATA_FROM_REMOTE_REPOSITORY + REMOTE_REPOSITORY + ERROR_TO_RECEIVE_2 + fileName);
        }
        return stringBuilder.toString();
    }
}
