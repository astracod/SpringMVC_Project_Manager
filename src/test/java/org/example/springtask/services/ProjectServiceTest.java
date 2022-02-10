package org.example.springtask.services;

import org.example.springtask.dto.*;
import org.example.springtask.entity.Project;
import org.example.springtask.entity.Role;
import org.example.springtask.entity.Task;
import org.example.springtask.entity.Worker;
import org.example.springtask.exception.RequestProcessingException;
import org.example.springtask.mapping.ProjectMapping;
import org.example.springtask.repository.FileRepository;
import org.example.springtask.repository.ProjectDAO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private static Clock clock;

    @Mock
    private FileRepository fileRepository;

    @Mock
    PasswordEncoder encoder;

    @Mock
    private ProjectMapping projectMapping;

    @Mock
    private ProjectDAO projectDAO;

    @InjectMocks
    private ProjectService projectService;


    private static final LocalDateTime DATE_TIME = LocalDateTime.now();

    public static final String GET_All_INFO_ABOUT_WORKER_SERVICE_CHECK_WITH_WRONG_ID = "ВНИМАНИЕ!!!  Исполнителя с таким ID нет в базе данных";
    public static final String GET_ALL_INFO_BY_PROJECT_ID_CHECK_WITH_WRONG_IG = "ВНИМАНИЕ!!!  Проекта с таким ID исполнителя нет в базе данных";
    private static final int WORKER_ID = 1;
    public static final String DELETE_FROM_TABLE_TASK_ON_DB = "Задача удалена из базы данных : " + WORKER_ID + " .";
    private static final String WORKER_FIRST_NAME = "User";
    private static final String WORKER_LAST_NAME = "Userov";
    private static final String LOGIN = "user@mail.ru";
    private static final String PASSWORD = "user";
    private static final String PROJECT_NAME = "Project Name";
    private static final String TASK_NAME = "Task Name";
    private static final String TASK_TEXT = "Task Text";
    private static final String SUCCESSFUL_STATUS_AFTER_SAVE_WORKER = "Исполнитель добавлен в базу данных.";
    private static final String ERROR_STATUS_AFTER_SAVE_WORKER = "Заполните все требуемые поля данных пользователя при регистрации.";
    private static final String SUCCESSFUL_STATUS_AFTER_REMOVE_WORKER_FROM_TASK = "Исполнитель удален из задачи.";
    private static final String SUCCESSFUL_STATUS_AFTER_REMOVE_WORKER_FROM_DATABASE = "Исполнитель удален из базы данных.";
    private static final String PROJECT_CREATED = " Проект создан.";
    private static final String PROJECT_DELETE = "Проект удален из базы данных.";
    private static final String REMOVE_TASK_FROM_DB = "Задача удалена из базы данных  ";
    private static final String PATH_TO_FILE = "path.txt";
    private static final String EXECUTOR_DATA_CHANGED = " Данные исполнителя изменены";
    private static final String CHANGE_PROJECT_NAME = "Имя проекта успешно заменено";
    private static final String REMOVE_WORKER_FROM_PROJECT = "Исполнитель удален из проекта.";
    private static final String TASK_FORMED_ON_A_REMOTE_RESOURCE = "Задача сформирована на удаленом ресурсе";
    private static final String TASK_CREATE_IN_DB = "Задача создана в базе данных.";
    private static final String FILE_PATH_WROTE = "Путь к файлу записан в Базу Данных";
    private static final String REFRESH_TASK = "Текст задачи обновлен в удаленном хранилище.";
    private static final Integer NEGATIVE_DATABASE_ANSWER = -1;
    private static final String THE_EXECUTOR_ASSIGNED_TO_TASK = " Исполнитель присвоен задаче";
    public static final String THE_EXECUTOR_REMOVED_FROM_TASK = " Исполнитель удален из задачи.";
    public static final String THE_TASK_DID_NOT_REMOVE = "Задача не удалена";
    public static final String WORD_TASK = " Задача : ";
    public static final String WORD_DELETED = " удалена.";
    public static final String SYMBOL_POINT = ".";
    public static final String FORMAT_FOR_TIME_AND_DATE = "yyyy-MM-dd HH:mm:ss";

    @Test
    void shouldGetAllInfoByWorkerIdThenNotProjects() {

        Worker returnWorker = new Worker(
                WORKER_ID, WORKER_FIRST_NAME, WORKER_LAST_NAME, LOGIN, PASSWORD, Role.USER, Set.of()
        );

        WorkerWithProjectsDto worker = new WorkerWithProjectsDto();
        worker.setId(WORKER_ID);
        worker.setFirstName(WORKER_FIRST_NAME);
        worker.setLastName(WORKER_LAST_NAME);
        worker.setProjects(List.of());

        when(projectDAO.getAllInfoByWorkerId(WORKER_ID)).thenReturn(returnWorker);
        when(projectDAO.getAllProjectTasksByWorkerId(WORKER_ID)).thenReturn(List.of());
        when(projectMapping.toFullWorker(returnWorker)).thenReturn(worker);

        FullWorkerDto expectedResult = new FullWorkerDto();
        expectedResult.setWorker(worker);
        expectedResult.setTasks(List.of());

        FullWorkerDto result = projectService.getAllInfoByWorkerId(WORKER_ID);
        assertEquals(expectedResult, result);
    }

    @Test
    void shouldGetAllInfoByWorkerIdWithProjects() {

        Worker returnWorker = new Worker(
                WORKER_ID, WORKER_FIRST_NAME, WORKER_LAST_NAME, LOGIN, PASSWORD, Role.USER, Set.of()
        );

        WorkerWithProjectsDto worker = new WorkerWithProjectsDto();
        worker.setId(WORKER_ID);
        worker.setFirstName(WORKER_FIRST_NAME);
        worker.setLastName(WORKER_LAST_NAME);
        ProjectInfoForWorkerDto projectInfo = new ProjectInfoForWorkerDto();
        projectInfo.setId(WORKER_ID);
        projectInfo.setProjectName(PROJECT_NAME);
        worker.setProjects(List.of(projectInfo));


        when(projectDAO.getAllInfoByWorkerId(WORKER_ID)).thenReturn(returnWorker);
        when(projectMapping.toFullWorker(returnWorker)).thenReturn(worker);

        Task task = new Task();
        TaskDto taskDto = new TaskDto();

        when(projectDAO.getAllProjectTasksByWorkerId(WORKER_ID)).thenReturn(List.of(task));
        when(projectMapping.toTaskDtoList(List.of(task))).thenReturn(List.of(taskDto));

        FullWorkerDto expectedResult = new FullWorkerDto();
        expectedResult.setWorker(worker);
        expectedResult.setTasks(List.of(taskDto));

        FullWorkerDto result = projectService.getAllInfoByWorkerId(WORKER_ID);

        assertThat(result).isEqualTo(expectedResult);

        verify(projectMapping, times(1)).toFullWorker(returnWorker);
    }

    @Test
    void shouldThrowIfGetAllInfoByWorkerIdThrow() {
        when(projectDAO.getAllInfoByWorkerId(WORKER_ID)).thenThrow(new RuntimeException());

        assertThatThrownBy(() -> projectService.getAllInfoByWorkerId(WORKER_ID))
                .withFailMessage(GET_All_INFO_ABOUT_WORKER_SERVICE_CHECK_WITH_WRONG_ID)
                .isInstanceOf(RequestProcessingException.class);

    }


    @Test
    void getAllExecutorProjectsByProjectIdThrow() {
        when(projectDAO.getAllInfoByProjectId(WORKER_ID)).thenThrow(new RuntimeException());

        assertThatThrownBy(() -> projectService.getAllExecutorProjectsByProjectId(WORKER_ID))
                .withFailMessage(GET_ALL_INFO_BY_PROJECT_ID_CHECK_WITH_WRONG_IG)
                .isInstanceOf(RequestProcessingException.class);
    }

    @Test
    @DisplayName("getAllExecutorProjectsByProjectId")
    void shouldReturnProjectDto() {
        LocalDateTime timeNow = DATE_TIME;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_FOR_TIME_AND_DATE);

        Worker returnWorker = new Worker(
                WORKER_ID, WORKER_FIRST_NAME, WORKER_LAST_NAME, LOGIN, PASSWORD, Role.USER, Set.of()
        );

        Project projectByProjectId = new Project();
        projectByProjectId.setId(WORKER_ID);
        projectByProjectId.setProjectName(PROJECT_NAME);
        projectByProjectId.setWorkers(Set.of(returnWorker));

        Task task = new Task();
        task.setId(WORKER_ID);
        task.setTaskName(TASK_NAME);
        task.setDateCreateTask(timeNow);
        task.setUserId(WORKER_ID);
        task.setProject(projectByProjectId);

        TaskDto taskDto = new TaskDto();
        taskDto.setId(WORKER_ID);
        taskDto.setTaskName(TASK_NAME);
        taskDto.setDateCreateTask(timeNow.format(formatter));
        taskDto.setUserId(WORKER_ID);
        taskDto.setProjectId(WORKER_ID);

        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(WORKER_ID);
        projectDto.setProjectName(PROJECT_NAME);
        projectDto.setWorkersId(List.of(returnWorker));

        Status status = new Status();
        status.setStatus(null);

        when(projectDAO.getAllInfoByProjectId(WORKER_ID)).thenReturn(projectByProjectId);
        when(projectMapping.toProject(projectByProjectId)).thenReturn(projectDto);

        when(projectDAO.getAllProjectTasksByProjectId(WORKER_ID)).thenReturn(List.of(task));
        when(projectMapping.toTaskDtoList(List.of(task))).thenReturn(List.of(taskDto));

        when(projectDAO.getFilePath(WORKER_ID)).thenReturn(status);
        when(fileRepository.getFileTaskByFileId(status.getStatus())).thenReturn(null);

        ProjectDto expectedProjectDto = new ProjectDto();
        expectedProjectDto.setId(WORKER_ID);
        expectedProjectDto.setProjectName(PROJECT_NAME);
        expectedProjectDto.setWorkersId(List.of(returnWorker));
        expectedProjectDto.setTasks(List.of(taskDto));


        ProjectDto result = projectService.getAllExecutorProjectsByProjectId(WORKER_ID);

        assertThat(result).isEqualTo(expectedProjectDto);
    }

    @Test
    @DisplayName("showAllUsers")
    void showAllUsers() {
        List<Worker> workers = List.of(
                new Worker(1, "User", "Userov", "user@mail.ru", "user", Role.USER, null),
                new Worker(2, "Admin", "Adminov", "admin@mail.ru", "admin", Role.ADMIN, null)
        );
        WorkerDto workerDto1 = new WorkerDto();
        workerDto1.setId(1);
        workerDto1.setFirstName("User");
        workerDto1.setLastName("Userov");
        workerDto1.setEmail("user@mail.ru");

        WorkerDto workerDto2 = new WorkerDto();
        workerDto2.setId(2);
        workerDto2.setFirstName("Admin");
        workerDto2.setLastName("Adminov");
        workerDto2.setEmail("admin@mail.ru");

        List<WorkerDto> workersDtoList = List.of(workerDto1, workerDto2);

        when(projectDAO.allWorkers()).thenReturn(workers);
        when(projectMapping.toWorkersDto(workers)).thenReturn(workersDtoList);

        List<WorkerDto> result = projectService.showAllUsers();

        assertThat(result).isEqualTo(workersDtoList);

    }

    @Test
    @DisplayName("saveWorker")
    void shouldReturnStatusAfterSuccessfulSaveWorker() {
        Status expectedStatus = new Status();
        expectedStatus.setStatus(SUCCESSFUL_STATUS_AFTER_SAVE_WORKER);

        String codPassword = encoder.encode(PASSWORD);

        when(projectDAO.createWorker(WORKER_FIRST_NAME, WORKER_LAST_NAME, LOGIN, codPassword)).thenReturn(expectedStatus);

        Status actualStatus = projectService.saveWorker(WORKER_FIRST_NAME, WORKER_LAST_NAME, LOGIN, PASSWORD);

        assertThat(actualStatus).isEqualTo(expectedStatus);
    }

    @Test
    @DisplayName("saveWorker")
    void shouldReturnStatusAfterSaveWorkerThrow() {
        Status expectedStatus = new Status();
        expectedStatus.setStatus(ERROR_STATUS_AFTER_SAVE_WORKER);

        Status actualStatus = projectService.saveWorker(null, null, null, null);

        assertThat(actualStatus).isEqualTo(expectedStatus);
    }

    @Test
    @DisplayName("removeWorker")
    void shouldReturnStatusAfterRemoveWorker() {
        LocalDateTime timeNow = DATE_TIME;

        Project projectByProjectId = new Project();
        projectByProjectId.setId(WORKER_ID);
        projectByProjectId.setProjectName(PROJECT_NAME);

        Task task = new Task();
        task.setId(WORKER_ID);
        task.setTaskName(TASK_NAME);
        task.setDateCreateTask(timeNow);
        task.setUserId(WORKER_ID);
        task.setProject(projectByProjectId);

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);

        Status statusForRemoveExecutorFromTask = new Status();
        statusForRemoveExecutorFromTask.setStatus(SUCCESSFUL_STATUS_AFTER_REMOVE_WORKER_FROM_TASK);

        Status statusForRemoveWorker = new Status();
        statusForRemoveWorker.setStatus(SUCCESSFUL_STATUS_AFTER_REMOVE_WORKER_FROM_DATABASE);

        Status expectedStatus = new Status();
        expectedStatus.setStatus(SUCCESSFUL_STATUS_AFTER_REMOVE_WORKER_FROM_TASK + '\u0020' + SUCCESSFUL_STATUS_AFTER_REMOVE_WORKER_FROM_DATABASE + '\u0020');

        when(projectDAO.returnSheetTask(WORKER_ID)).thenReturn(tasks);
        when(projectDAO.removeExecutorFromTask(task.getId(), WORKER_ID)).thenReturn(statusForRemoveExecutorFromTask);
        when(projectDAO.removeWorker(WORKER_ID)).thenReturn(statusForRemoveWorker);

        Status actualStatus = projectService.removeWorker(WORKER_ID);

        assertThat(actualStatus.getStatus()).isEqualTo(expectedStatus.getStatus());
    }

    @Test
    @DisplayName("showAllProjects")
    void shouldReturnOnlyProjectInfoDtoList() {
        Project projectForAllProjects = new Project();
        projectForAllProjects.setId(WORKER_ID);
        projectForAllProjects.setProjectName(PROJECT_NAME);
        List<Project> projectForAllProjectsList = List.of(projectForAllProjects);

        OnlyProjectInfoDto onlyProjectInfoDtoForAllProjects = new OnlyProjectInfoDto();
        onlyProjectInfoDtoForAllProjects.setId(WORKER_ID);
        onlyProjectInfoDtoForAllProjects.setProjectName(PROJECT_NAME);
        List<OnlyProjectInfoDto> expectedList = List.of(onlyProjectInfoDtoForAllProjects);

        when(projectDAO.allProjects()).thenReturn(projectForAllProjectsList);
        when(projectMapping.toOnlyProjectsInfoDto(projectForAllProjectsList)).thenReturn(expectedList);

        List<OnlyProjectInfoDto> result = projectService.showAllProjects();

        assertThat(result).isEqualTo(expectedList);
    }

    @Test
    @DisplayName("createProject")
    void shouldReturnStatusAfterCreateProject() {
        Status expendStatus = new Status();
        expendStatus.setStatus(PROJECT_CREATED);

        when(projectDAO.createProject(TASK_NAME)).thenReturn(expendStatus);

        Status result = projectService.createProject(TASK_NAME);

        assertThat(result).isEqualTo(expendStatus);
    }

    @Test
    @DisplayName("removeProject")
    void shouldReturnStatusAfterRemoveProject() {
        Task taskForProject = new Task();
        taskForProject.setId(WORKER_ID);
        taskForProject.setTaskName(TASK_NAME);
        taskForProject.setDateCreateTask(null);
        taskForProject.setUserId(WORKER_ID);
        Set<Task> tasks = Set.of(taskForProject);

        Project projectForGetProjectForDeleteTask = new Project();
        projectForGetProjectForDeleteTask.setId(WORKER_ID);
        projectForGetProjectForDeleteTask.setProjectName(PROJECT_NAME);
        projectForGetProjectForDeleteTask.setTasks(tasks);

        Status removeProject = new Status();
        removeProject.setStatus(PROJECT_DELETE);

        Status removeTaskFromDB = new Status();
        removeTaskFromDB.setStatus(REMOVE_TASK_FROM_DB);

        Status pathFromDeleteFile = new Status();
        pathFromDeleteFile.setStatus(PATH_TO_FILE);

        Status expected = new Status();
        expected.setStatus(removeProject.getStatus() + " , " + removeTaskFromDB.getStatus() + "\n");

        when(projectDAO.getProjectForDeleteTask(WORKER_ID)).thenReturn(projectForGetProjectForDeleteTask);
        when(projectDAO.removeProject(WORKER_ID)).thenReturn(removeProject);
        when(projectDAO.removeTask(WORKER_ID)).thenReturn(removeTaskFromDB);
        when(projectDAO.deleteFile(WORKER_ID)).thenReturn(pathFromDeleteFile);
        when(fileRepository.deleteFileTask(PATH_TO_FILE)).thenReturn(true);

        Status result = projectService.removeProject(WORKER_ID);

        assertThat(result.getStatus()).isEqualTo(expected.getStatus());
    }

    @Test
    @DisplayName("addProjectExecutor")
    void shouldReturnStatusAfterAddProjectExecutor() {
        Status expectedStatus = new Status();
        expectedStatus.setStatus(EXECUTOR_DATA_CHANGED);
        when(projectDAO.addProjectExecutor(WORKER_ID, WORKER_ID)).thenReturn(expectedStatus);
        Status result = projectService.addProjectExecutor(WORKER_ID, WORKER_ID);
        assertThat(result).isEqualTo(expectedStatus);
    }

    @Test
    @DisplayName("changeProjectName")
    void shouldStatusAfterChangeProjectName() {
        Status expected = new Status();
        expected.setStatus(CHANGE_PROJECT_NAME);
        when(projectDAO.changeProjectName(WORKER_ID, TASK_NAME)).thenReturn(expected);
        Status result = projectService.changeProjectName(WORKER_ID, TASK_NAME);
        assertThat(result).isEqualTo(expected);
    }


    @Test
    @DisplayName("removeWorkerFromProject")
    void shouldReturnStatusAfterRemoveWorkerFromProject() {
        Status expected = new Status();
        expected.setStatus(REMOVE_WORKER_FROM_PROJECT);
        when(projectDAO.removeWorkerFromProject(WORKER_ID, WORKER_ID)).thenReturn(expected);
        Status result = projectService.removeWorkerFromProject(WORKER_ID, WORKER_ID);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("getAllTasks")
    void shouldReturnListTasks() {
        Task taskForProject = new Task();
        taskForProject.setId(WORKER_ID);
        taskForProject.setTaskName(TASK_NAME);
        taskForProject.setDateCreateTask(null);
        taskForProject.setUserId(WORKER_ID);
        List<Task> tasks = List.of(taskForProject);

        TaskDto taskDto = new TaskDto();
        taskDto.setId(WORKER_ID);
        taskDto.setTaskName(TASK_NAME);
        taskDto.setDateCreateTask(null);
        taskDto.setUserId(WORKER_ID);
        List<TaskDto> expected = List.of(taskDto);


        when(projectDAO.getAllTasks()).thenReturn(tasks);
        when(projectMapping.toTaskDtoList(tasks)).thenReturn(expected);
        List result = projectService.getAllTasks();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("getTask")
    void shouldReturnTask() {
        Task task = new Task();
        task.setId(WORKER_ID);
        task.setTaskName(TASK_NAME);
        task.setDateCreateTask(null);
        task.setUserId(WORKER_ID);

        TaskDto taskDto = new TaskDto();
        taskDto.setId(WORKER_ID);
        taskDto.setTaskName(TASK_NAME);
        taskDto.setDateCreateTask(null);
        taskDto.setUserId(WORKER_ID);

        when(projectDAO.getTask(WORKER_ID)).thenReturn(task);
        when(projectMapping.toTaskDto(task)).thenReturn(taskDto);

        TaskDto result = projectService.getTask(WORKER_ID);

        assertThat(result).isEqualTo(taskDto);
    }

    @Test
    @DisplayName("createTask")
    void shouldReturnStatusAfterRefreshTask() {
        Status refreshStatus = new Status();
        refreshStatus.setStatus(REFRESH_TASK);

        LocalDateTime defaultLocalDateTime = LocalDateTime.of(2020, 1, 1, 12, 0, 25);
        Clock fixedClock = Clock.fixed(defaultLocalDateTime.toInstant(ZoneOffset.UTC), ZoneId.of("UTC"));
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());

        LocalDateTime myTime = LocalDateTime.now(fixedClock);


        String answerDate = myTime.toString().substring(0, myTime.toString().indexOf("T"))
                .concat(" ")
                .concat(myTime.toString().substring(11, 19));


        Status expected = new Status();
        expected.setStatus(refreshStatus.getStatus() + " : " + answerDate);


        when(projectDAO.getTaskByName(TASK_NAME)).thenReturn(WORKER_ID);
        when(projectDAO.refreshTask(WORKER_ID, TASK_NAME, myTime, WORKER_ID)).thenReturn(refreshStatus);
        //when(fileRepository.giveTask(myTime, TASK_TEXT, TASK_NAME)).thenReturn(statusForGiveTaskFileRepository);

        Status result = projectService.createTask(TASK_TEXT, TASK_NAME, WORKER_ID);

        assertThat(result.getStatus()).isEqualTo(expected.getStatus());

    }

    @Test
    @DisplayName("createTask")
    void shouldReturnStatusAfterCreateTask() {
        Status statusForGiveTaskFileRepository = new Status();
        Map<String, String> fileNameFromTask = new HashMap<>();
        fileNameFromTask.put("fileName", PATH_TO_FILE);
        statusForGiveTaskFileRepository.setStatus(TASK_FORMED_ON_A_REMOTE_RESOURCE);
        statusForGiveTaskFileRepository.setAuxiliaryField(fileNameFromTask);

        Status taskCreate = new Status();
        taskCreate.setStatus(TASK_CREATE_IN_DB);

        Status filePathWroteInDB = new Status();
        filePathWroteInDB.setStatus(FILE_PATH_WROTE);

        LocalDateTime defaultLocalDateTime = LocalDateTime.of(2020, 1, 1, 12, 0, 25);
        Clock fixedClock = Clock.fixed(defaultLocalDateTime.toInstant(ZoneOffset.UTC), ZoneId.of("UTC"));
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());

        LocalDateTime myTime = LocalDateTime.now(fixedClock);

        String answerDate = myTime.toString().substring(0, myTime.toString().indexOf("T"))
                .concat(" ")
                .concat(myTime.toString().substring(11, 19));

        Status expected = new Status();
        expected.setStatus(statusForGiveTaskFileRepository.getStatus() + " ; " + taskCreate.getStatus() + " ; " + filePathWroteInDB.getStatus() + " : " + answerDate);

        when(projectDAO.getTaskByName(TASK_NAME)).thenReturn(NEGATIVE_DATABASE_ANSWER).thenReturn(WORKER_ID);

        when(projectDAO.createTask(TASK_NAME, myTime, WORKER_ID)).thenReturn(taskCreate);

        when(fileRepository.giveTask(myTime, TASK_TEXT, TASK_NAME)).thenReturn(statusForGiveTaskFileRepository);

        when(projectDAO.createFile(WORKER_ID, PATH_TO_FILE)).thenReturn(filePathWroteInDB);

        Status result = projectService.createTask(TASK_TEXT, TASK_NAME, WORKER_ID);

        assertThat(result.getStatus()).isEqualTo(expected.getStatus());
    }

    @Test
    @DisplayName("removeTask")
    void shouldReturnSuccessfulStatusAfterRemoveTask() {
        Status deleteTaskFromTableTask = new Status();
        deleteTaskFromTableTask.setStatus(DELETE_FROM_TABLE_TASK_ON_DB);

        Status deleteFileFromTableFile = new Status();
        deleteFileFromTableFile.setStatus(PATH_TO_FILE);

        Status expended = new Status();
        expended.setStatus(WORD_TASK + deleteFileFromTableFile.getStatus().substring(0, deleteFileFromTableFile.getStatus().indexOf(SYMBOL_POINT)) + WORD_DELETED);

        when(projectDAO.removeTask(WORKER_ID)).thenReturn(deleteTaskFromTableTask);
        when(projectDAO.deleteFile(WORKER_ID)).thenReturn(deleteFileFromTableFile);
        when(fileRepository.deleteFileTask(deleteFileFromTableFile.getStatus())).thenReturn(true);

        Status result = projectService.removeTask(WORKER_ID);

        assertThat(result).isEqualTo(expended);
    }

    @Test
    @DisplayName("removeTask")
    void shouldReturnStatusAfterUnsuccessfulRemoveTask() {
        Status deleteTaskFromTableTask = new Status();
        deleteTaskFromTableTask.setStatus(DELETE_FROM_TABLE_TASK_ON_DB);

        Status deleteFileFromTableFile = new Status();
        deleteFileFromTableFile.setStatus(PATH_TO_FILE);

        Status expended = new Status();
        expended.setStatus(THE_TASK_DID_NOT_REMOVE);

        when(projectDAO.removeTask(WORKER_ID)).thenReturn(deleteTaskFromTableTask);
        when(projectDAO.deleteFile(WORKER_ID)).thenReturn(deleteFileFromTableFile);
        when(fileRepository.deleteFileTask(deleteFileFromTableFile.getStatus())).thenReturn(false);

        Status result = projectService.removeTask(WORKER_ID);

        assertThat(result).isEqualTo(expended);
    }

    @Test
    @DisplayName("assignAnExecutorToTask")
    void shouldReturnStatusAfterSuccessfulAssignedExecutorToTask() {
        Status expended = new Status();
        expended.setStatus(THE_EXECUTOR_ASSIGNED_TO_TASK);

        when(projectDAO.assignAnExecutorToTask(WORKER_ID, WORKER_ID)).thenReturn(expended);

        Status result = projectService.assignAnExecutorToTask(WORKER_ID, WORKER_ID);

        assertThat(result).isEqualTo(expended);
    }

    @Test
    @DisplayName("removeExecutorFromTask")
    void removeExecutorFromTask() {
        Status expended = new Status();
        expended.setStatus(THE_EXECUTOR_REMOVED_FROM_TASK);

        when(projectDAO.removeExecutorFromTask(WORKER_ID, WORKER_ID)).thenReturn(expended);

        Status result = projectService.removeExecutorFromTask(WORKER_ID, WORKER_ID);

        assertThat(result).isEqualTo(expended);
    }
}























