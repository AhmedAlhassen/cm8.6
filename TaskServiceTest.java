package com.camunda.engine.service;


import io.camunda.tasklist.CamundaTaskListClient;
import io.camunda.tasklist.dto.*;
import io.camunda.tasklist.exception.TaskListException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @Mock
    private CamundaTaskListClient client;

    @InjectMocks
    private TaskService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new TaskService(client);
    }

    @Test
    void findTasksForProcessInstanceId_success() throws TaskListException {
        TaskList taskList = mock(TaskList.class);
        List<Task> tasks = List.of(mock(Task.class));
        when(client.getTasks(any(TaskSearch.class))).thenReturn(taskList);
        when(taskList.getItems()).thenReturn(tasks);

        List<Task> result = service.findTasksForProcessInstanceId("procId");
        assertEquals(tasks, result);
    }

    @Test
    void findTasksForProcessInstanceId_withParams_success() throws TaskListException {
        TaskList taskList = mock(TaskList.class);
        when(client.getTasks(any(TaskSearch.class))).thenReturn(taskList);
        when(taskList.getItems()).thenReturn(List.of());

        List<Task> result = service.findTasksForProcessInstanceId("procId", TaskState.CREATED, true);
        assertNotNull(result);
    }

    @Test
    void findActiveTasksForVariable_success() throws TaskListException {
        TaskList taskList = mock(TaskList.class);
        List<Task> tasks = List.of(mock(Task.class));
        when(client.getTasks(any(TaskSearch.class))).thenReturn(taskList);
        when(taskList.getItems()).thenReturn(tasks);

        List<Task> result = service.findActiveTasksForVariable("name", "value");
        assertEquals(tasks, result);
    }

    @Test
    void findTaskById_nullId_returnsEmpty() {
        assertTrue(service.findTaskById(null).isEmpty());
    }

    @Test
    void findTaskById_notFound_returnsEmpty() throws TaskListException {
        when(client.getTask(anyString(), eq(true))).thenThrow(new TaskListException("not found"));
        assertTrue(service.findTaskById("1").isEmpty());
    }

    @Test
    void findTaskById_success() throws TaskListException {
        Task task = mock(Task.class);
        when(client.getTask(eq("1"), eq(true))).thenReturn(task);
        Optional<Task> result = service.findTaskById("1");
        assertTrue(result.isPresent());
        assertEquals(task, result.get());
    }

    @Test
    void findTaskByIdWithVars_success() throws TaskListException {
        Task task = mock(Task.class);
        when(client.getTask(eq("2"), eq(false))).thenReturn(task);
        Optional<Task> result = service.findTaskById("2", false);
        assertTrue(result.isPresent());
        assertEquals(task, result.get());
    }

    @Test
    void findTaskByIdOrThrow_throwsIfNull() {
        assertThrows(TaskListException.class, () -> service.findTaskByIdOrThrow(null));
    }

    @Test
    void findTaskByIdOrThrow_throwsIfNotFound() throws TaskListException {
        when(client.getTask(anyString(), anyBoolean())).thenReturn(null);
        assertThrows(TaskListException.class, () -> service.findTaskByIdOrThrow("notfound"));
    }

    @Test
    void findTaskByIdOrThrow_success() throws TaskListException {
        Task task = mock(Task.class);
        when(client.getTask(eq("tid"), eq(true))).thenReturn(task);
        assertEquals(task, service.findTaskByIdOrThrow("tid"));
    }

    @Test
    void taskExists_true() throws TaskListException {
        when(client.getTask(eq("id"), eq(false))).thenReturn(mock(Task.class));
        assertTrue(service.taskExists("id"));
    }

    @Test
    void taskExists_false() throws TaskListException {
        when(client.getTask(eq("id"), eq(false))).thenReturn(null);
        assertFalse(service.taskExists("id"));
    }

    @Test
    void findAllByAssigneeAndState_success() throws TaskListException {
        TaskList taskList = mock(TaskList.class);
        when(client.getTasks(any(TaskSearch.class))).thenReturn(taskList);
        when(taskList.getItems()).thenReturn(List.of(mock(Task.class)));
        List<Task> result = service.findAllByAssigneeAndState("user", TaskState.CREATED);
        assertFalse(result.isEmpty());
    }

    @Test
    void findAllByAssigneeAndState_emptyList() throws TaskListException {
        TaskList taskList = mock(TaskList.class);
        when(client.getTasks(any(TaskSearch.class))).thenReturn(taskList);
        when(taskList.getItems()).thenReturn(null);
        List<Task> result = service.findAllByAssigneeAndState("user", TaskState.CREATED);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByAssigneeAndStateSafe_returnsEmptyOnException() throws TaskListException {
        TaskService spyService = Mockito.spy(service);
        doThrow(new TaskListException("error")).when(spyService).findAllByAssigneeAndState(anyString(), any());
        List<Task> result = spyService.findAllByAssigneeAndStateSafe("user", TaskState.CREATED);
        assertTrue(result.isEmpty());
    }

    @Test
    void countTasksByAssigneeAndState_success() throws TaskListException {
        TaskService spyService = Mockito.spy(service);
        doReturn(List.of(mock(Task.class), mock(Task.class)))
                .when(spyService).findAllByAssigneeAndState(anyString(), any(), eq(false));
        assertEquals(2, spyService.countTasksByAssigneeAndState("user", TaskState.CREATED));
    }

    @Test
    void claimTask_success() throws TaskListException {
        Task task = mock(Task.class);
        when(client.claim(anyString(), anyString())).thenReturn(task);
        assertEquals(task, service.claimTask("tid", "assignee"));
    }

    @Test
    void claimTask_nullTask_throws() throws TaskListException {
        when(client.claim(anyString(), anyString())).thenReturn(null);
        assertThrows(TaskListException.class, () -> service.claimTask("tid", "assignee"));
    }

    @Test
    void claimTaskForCurrentUser_success() throws TaskListException {
        TaskService spyService = Mockito.spy(service);
        doReturn("user").when(spyService).getCurrentUser();
        doReturn(mock(Task.class)).when(spyService).claimTask(anyString(), anyString());
        assertNotNull(spyService.claimTaskForCurrentUser("tid"));
    }

    @Test
    void canClaimTask_success() {
        TaskService spyService = Mockito.spy(service);
        Task task = mock(Task.class);
        when(task.getTaskState()).thenReturn(TaskState.CREATED);
        when(task.getAssignee()).thenReturn("someoneElse");
        doReturn(Optional.of(task)).when(spyService).findTaskById(anyString());
        assertTrue(spyService.canClaimTask("tid", "user"));
    }
}
