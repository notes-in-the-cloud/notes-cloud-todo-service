package com.notescloud.todo_service;

import com.notescloud.todo_service.domain.TodoList;
import com.notescloud.todo_service.domain.TodoPriority;
import com.notescloud.todo_service.domain.TodoTask;
import com.notescloud.todo_service.dto.CreateTodoListRequest;
import com.notescloud.todo_service.dto.UpdateTodoListRequest;
import com.notescloud.todo_service.exception.ResourceNotFoundException;
import com.notescloud.todo_service.repository.TodoListRepository;
import com.notescloud.todo_service.repository.TodoTaskRepository;
import com.notescloud.todo_service.service.TodoListService;
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
class TodoListServiceTest {

    @Mock
    private TodoListRepository todoListRepository;

    @Mock
    private TodoTaskRepository todoTaskRepository;

    @InjectMocks
    private TodoListService todoListService;

    @Test
    void createTodoList_savesAndReturnsResponse() {
        UUID userId = UUID.randomUUID();

        CreateTodoListRequest request = new CreateTodoListRequest(
            userId,
            "University tasks"
        );

        when(todoListRepository.save(any(TodoList.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        var response = todoListService.createTodoList(request);

        ArgumentCaptor<TodoList> listCaptor = ArgumentCaptor.forClass(TodoList.class);
        verify(todoListRepository).save(listCaptor.capture());

        TodoList savedList = listCaptor.getValue();

        assertThat(savedList.userId()).isEqualTo(userId);
        assertThat(savedList.title()).isEqualTo("University tasks");
        assertThat(response.title()).isEqualTo("University tasks");
    }

    @Test
    void updateTodoList_updatesTitle() {
        UUID listId = UUID.randomUUID();

        TodoList list = new TodoList(
            UUID.randomUUID(),
            "Old title"
        );

        UpdateTodoListRequest request = new UpdateTodoListRequest("New title");

        when(todoListRepository.findById(listId)).thenReturn(Optional.of(list));
        when(todoListRepository.save(list)).thenReturn(list);

        var response = todoListService.updateTodoList(listId, request);

        assertThat(response.title()).isEqualTo("New title");
    }

    @Test
    void updateTodoList_whenListDoesNotExist_throwsNotFound() {
        UUID listId = UUID.randomUUID();

        when(todoListRepository.findById(listId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoListService.updateTodoList(listId, new UpdateTodoListRequest("New title")))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Todo list not found with id");
    }

    @Test
    void getTodoList_returnsListWithOnlyNotDoneTasks() {
        UUID listId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        TodoList list = new TodoList(userId, "Project tasks");

        TodoTask task = new TodoTask(
            listId,
            userId,
            "Task 1",
            TodoPriority.HIGH,
            LocalDateTime.now().plusDays(1)
        );

        when(todoListRepository.findById(listId)).thenReturn(Optional.of(list));
        when(todoTaskRepository.findAllByListIdAndDoneFalse(list.id())).thenReturn(List.of(task));

        var response = todoListService.getTodoList(listId);

        assertThat(response.title()).isEqualTo("Project tasks");
        assertThat(response.tasks()).hasSize(1);
        assertThat(response.tasks().getFirst().title()).isEqualTo("Task 1");
    }

    @Test
    void getTodoList_whenListDoesNotExist_throwsNotFound() {
        UUID listId = UUID.randomUUID();

        when(todoListRepository.findById(listId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoListService.getTodoList(listId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Todo list not found with id");
    }

    @Test
    void deleteTodoList_whenListDoesNotExist_throwsNotFound() {
        UUID listId = UUID.randomUUID();

        when(todoListRepository.existsById(listId)).thenReturn(false);

        assertThatThrownBy(() -> todoListService.deleteTodoList(listId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Todo list not found with id");

        verify(todoListRepository, never()).deleteById(any());
    }
}