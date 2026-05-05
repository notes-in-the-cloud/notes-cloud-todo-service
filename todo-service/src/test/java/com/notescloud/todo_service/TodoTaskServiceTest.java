package com.notescloud.todo_service;

import com.notescloud.todo_service.domain.TodoPriority;
import com.notescloud.todo_service.domain.TodoTask;
import com.notescloud.todo_service.dto.CreateTodoTaskRequest;
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

        CreateTodoTaskRequest request = new CreateTodoTaskRequest(
            null,
            userId,
            "Standalone task",
            TodoPriority.HIGH,
            LocalDateTime.now().plusDays(1)
        );

        when(todoTaskRepository.save(any(TodoTask.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        todoTaskService.createTodoTask(request);

        ArgumentCaptor<TodoTask> taskCaptor = ArgumentCaptor.forClass(TodoTask.class);
        verify(todoTaskRepository).save(taskCaptor.capture());

        TodoTask savedTask = taskCaptor.getValue();

        assertThat(savedTask.listId()).isNull();
        assertThat(savedTask.userId()).isEqualTo(userId);
        assertThat(savedTask.title()).isEqualTo("Standalone task");
        assertThat(savedTask.priority()).isEqualTo(TodoPriority.HIGH);
        assertThat(savedTask.done()).isFalse();
    }

    @Test
    void createTodoTask_withMissingList_throwsNotFound() {
        UUID listId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        CreateTodoTaskRequest request = new CreateTodoTaskRequest(
            listId,
            userId,
            "Task inside list",
            TodoPriority.MEDIUM,
            LocalDateTime.now().plusDays(1)
        );

        when(todoListRepository.existsById(listId)).thenReturn(false);

        assertThatThrownBy(() -> todoTaskService.createTodoTask(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Todo list not found with id");

        verify(todoTaskRepository, never()).save(any());
    }

    @Test
    void updateTodoTask_updatesOnlyProvidedFields() {
        UUID taskId = UUID.randomUUID();
        TodoTask existingTask = new TodoTask(
            null,
            UUID.randomUUID(),
            "Old title",
            TodoPriority.LOW,
            LocalDateTime.now().plusDays(1)
        );

        UpdateTodoTaskRequest request = new UpdateTodoTaskRequest(
            "New title",
            TodoPriority.HIGH,
            null
        );

        when(todoTaskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(todoTaskRepository.save(existingTask)).thenReturn(existingTask);

        var response = todoTaskService.updateTodoTask(taskId, request);

        assertThat(response.title()).isEqualTo("New title");
        assertThat(response.priority()).isEqualTo(TodoPriority.HIGH);
    }

    @Test
    void updateTodoTask_whenTaskDoesNotExist_throwsNotFound() {
        UUID taskId = UUID.randomUUID();

        UpdateTodoTaskRequest request = new UpdateTodoTaskRequest(
            "New title",
            TodoPriority.HIGH,
            LocalDateTime.now().plusDays(1)
        );

        when(todoTaskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoTaskService.updateTodoTask(taskId, request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Todo task not found with id");
    }

    @Test
    void markDone_marksTaskAsDone() {
        UUID taskId = UUID.randomUUID();

        TodoTask task = new TodoTask(
            null,
            UUID.randomUUID(),
            "Task",
            TodoPriority.MEDIUM,
            LocalDateTime.now().plusDays(1)
        );

        when(todoTaskRepository.findById(taskId)).thenReturn(Optional.of(task));

        todoTaskService.markDone(taskId);

        assertThat(task.done()).isTrue();
        verify(todoTaskRepository).save(task);
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

        var result = todoTaskService.getStandaloneTasks(userId);

        assertThat(result)
            .extracting("title")
            .containsExactly("No due date", "Future task");
    }
}