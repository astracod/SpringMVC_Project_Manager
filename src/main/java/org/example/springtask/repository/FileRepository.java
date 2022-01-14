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

    public final String REMOTE_REPOSITORY = "C:\\Users\\Admin\\Desktop\\scp";

    public Status giveTask(LocalDateTime dateCreateTask, String text, String taskName) {

        String newText = dateCreateTask + "\n" + text + "\n";
        byte[] textTask = newText.getBytes();
        Status status = new Status();
        File uploadDirectory = new File(REMOTE_REPOSITORY);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdir();
        }

        String fileName = taskName.replace(" ", "_") + ".txt";
        String lastPath = uploadDirectory + "\\" + fileName;
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
            status.setStatus("Ошибка передачи задачи в хранилище данных");
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
        status.setStatus("Задача сформирована на удаленом ресурсе");
        status.setAuxiliaryField(fileNameFromTask);
        return status;
    }

    public boolean deleteFileTask(String name) {

        String absolutePath = REMOTE_REPOSITORY + "\\" + name;
        Path path = Paths.get(absolutePath);

        boolean res = Files.exists(path);
        if (res) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return res;
    }

    public String getFileTaskByFileId(String fileName) {
        String absolutePath = REMOTE_REPOSITORY + "\\" + fileName;
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        try (BufferedReader reader = new BufferedReader(new FileReader(absolutePath))) {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }
}

























