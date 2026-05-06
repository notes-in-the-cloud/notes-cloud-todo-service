package com.notescloud.todo_service;

import com.notescloud.todo_service.domain.TodoPriority;
import com.notescloud.todo_service.domain.TodoTask;
import com.notescloud.todo_service.dto.CreateTodoTaskRequest;
import com.notescloud.todo_service.dto.TodoTaskResponse;
import com.notescloud.todo_service.dto.UpdateTodoTaskRequest;
import com.notescloud.todo_service.exception.ResourceNotFoundException;
import com.notescloud.todo_service.repository.TodoListRepository;
import com.notescloud.todo_service.repository.TodoTaskRepository;
import com.notescloud.todo_service.service.TodoTaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoTaskServiceTest {

    @Mock
    private TodoTaskRepository todoTaskRepository;

    @Mock
    private TodoListRepository todoListRepository;

    @InjectMocks
    private TodoTaskService todoTaskService;

    @Test
    void createTodoTask_withoutList_createsStandaloneTask() {
        UUID userId = UUID.randomUUID();
        LocalDateTime dueDate = LocalDateTime.now().plusDays(1);

        CreateTodoTaskRequest request = new CreateTodoTaskRequest(
            null,
            "Standalone task",
            TodoPriority.HIGH,
            dueDate
        );

        when(todoTaskRepository.save(any(TodoTask.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        todoTaskService.createTodoTask(userId, request);

        ArgumentCaptor<TodoTask> captor = ArgumentCaptor.forClass(TodoTask.class);
        verify(todoTaskRepository).save(captor.capture());

        TodoTask savedTask = captor.getValue();

        assertThat(savedTask.listId()).isNull();
        assertThat(savedTask.userId()).isEqualTo(userId);
        assertThat(savedTask.title()).isEqualTo("Standalone task");
        assertThat(savedTask.priority()).isEqualTo(TodoPriority.HIGH);
        assertThat(savedTask.dueDate()).isEqualTo(dueDate);
        assertThat(savedTask.done()).isFalse();
    }

    @Test
    void createTodoTask_withExistingList_createsTaskInsideList() {
        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();

        CreateTodoTaskRequest request = new CreateTodoTaskRequest(
            listId,
            "Task inside list",
            TodoPriority.MEDIUM,
            LocalDateTime.now().plusDays(1)
        );

        when(todoListRepository.existsById(listId)).thenReturn(true);
        when(todoTaskRepository.save(any(TodoTask.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        todoTaskService.createTodoTask(userId, request);

        ArgumentCaptor<TodoTask> captor = ArgumentCaptor.forClass(TodoTask.class);
        verify(todoTaskRepository).save(captor.capture());

        TodoTask savedTask = captor.getValue();

        assertThat(savedTask.listId()).isEqualTo(listId);
        assertThat(savedTask.userId()).isEqualTo(userId);
        assertThat(savedTask.title()).isEqualTo("Task inside list");
        assertThat(savedTask.priority()).isEqualTo(TodoPriority.MEDIUM);
    }

    @Test
    void createTodoTask_withMissingList_throwsNotFound() {
        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();

        CreateTodoTaskRequest request = new CreateTodoTaskRequest(
            listId,
            "Task inside list",
            TodoPriority.MEDIUM,
            LocalDateTime.now().plusDays(1)
        );

        when(todoListRepository.existsById(listId)).thenReturn(false);

        assertThatThrownBy(() -> todoTaskService.createTodoTask(userId, request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Todo list not found with id");

        verify(todoTaskRepository, never()).save(any());
    }

    @Test
    void updateTodoTask_updatesTaskWhenItBelongsToUser() {
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        TodoTask task = new TodoTask(
            null,
            userId,
            "Old title",
            TodoPriority.LOW,
            LocalDateTime.now().plusDays(1)
        );

        UpdateTodoTaskRequest request = new UpdateTodoTaskRequest(
            "New title",
            TodoPriority.HIGH,
            null,
            false
        );

        when(todoTaskRepository.findByIdAndUserId(taskId, userId))
            .thenReturn(Optional.of(task));

        when(todoTaskRepository.save(task))
            .thenReturn(task);

        var response = todoTaskService.updateTodoTask(userId, taskId, request);

        assertThat(response.title()).isEqualTo("New title");
        assertThat(response.priority()).isEqualTo(TodoPriority.HIGH);
    }

    @Test
    void updateTodoTask_whenTaskDoesNotBelongToUser_throwsNotFound() {
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        UpdateTodoTaskRequest request = new UpdateTodoTaskRequest(
            "New title",
            TodoPriority.HIGH,
            LocalDateTime.now().plusDays(1),
            false
        );

        when(todoTaskRepository.findByIdAndUserId(taskId, userId))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoTaskService.updateTodoTask(userId, taskId, request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Todo task not found with id");

        verify(todoTaskRepository, never()).save(any());
    }

    @Test
    void getTodoTask_returnsTaskWhenItBelongsToUser() {
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        TodoTask task = new TodoTask(
            null,
            userId,
            "Task",
            TodoPriority.MEDIUM,
            LocalDateTime.now().plusDays(1)
        );

        when(todoTaskRepository.findByIdAndUserId(taskId, userId))
            .thenReturn(Optional.of(task));

        var response = todoTaskService.getTodoTask(userId, taskId);

        assertThat(response.title()).isEqualTo("Task");
        assertThat(response.userId()).isEqualTo(userId);
    }

    @Test
    void deleteTodoTask_deletesTaskWhenItBelongsToUser() {
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        TodoTask task = new TodoTask(
            null,
            userId,
            "Task",
            TodoPriority.MEDIUM,
            LocalDateTime.now().plusDays(1)
        );

        when(todoTaskRepository.findByIdAndUserId(taskId, userId))
            .thenReturn(Optional.of(task));

        todoTaskService.deleteTodoTask(userId, taskId);

        verify(todoTaskRepository).delete(task);
    }

    @Test
    void deleteTodoTask_whenTaskDoesNotBelongToUser_throwsNotFound() {
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        when(todoTaskRepository.findByIdAndUserId(taskId, userId))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoTaskService.deleteTodoTask(userId, taskId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Todo task not found with id");

        verify(todoTaskRepository, never()).delete(any());
    }

    @Test
    void getStandaloneTasks_returnsOnlyNotExpiredTasks() {
        UUID userId = UUID.randomUUID();

        TodoTask noDueDateTask = new TodoTask(
            null,
            userId,
            "No due date",
            TodoPriority.MEDIUM,
            null
        );

        TodoTask futureTask = new TodoTask(
            null,
            userId,
            "Future task",
            TodoPriority.HIGH,
            LocalDateTime.now().plusDays(2)
        );

        TodoTask expiredTask = new TodoTask(
            null,
            userId,
            "Expired task",
            TodoPriority.LOW,
            LocalDateTime.now().minusDays(1)
        );

        when(todoTaskRepository.findAllByUserIdAndDoneFalseAndListIdIsNull(userId))
            .thenReturn(List.of(noDueDateTask, futureTask, expiredTask));

        List<TodoTaskResponse> result = todoTaskService.getStandaloneTasks(userId);

        assertThat(result)
            .extracting(TodoTaskResponse::title)
            .containsExactly("No due date", "Future task");
    }
}