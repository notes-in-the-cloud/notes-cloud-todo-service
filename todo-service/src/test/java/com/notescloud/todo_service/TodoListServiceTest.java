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
import org.springframework.test.util.ReflectionTestUtils;

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
            "University tasks"
        );

        when(todoListRepository.save(any(TodoList.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        var response = todoListService.createTodoList(userId, request);

        ArgumentCaptor<TodoList> captor = ArgumentCaptor.forClass(TodoList.class);
        verify(todoListRepository).save(captor.capture());

        TodoList savedList = captor.getValue();

        assertThat(savedList.userId()).isEqualTo(userId);
        assertThat(savedList.title()).isEqualTo("University tasks");
        assertThat(response.title()).isEqualTo("University tasks");
    }

    @Test
    void updateTodoList_updatesTitleWhenItBelongsToUser() {
        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();

        TodoList list = new TodoList(userId, "Old title");

        UpdateTodoListRequest request = new UpdateTodoListRequest("New title");

        when(todoListRepository.findByIdAndUserId(listId, userId))
            .thenReturn(Optional.of(list));

        when(todoListRepository.save(list))
            .thenReturn(list);

        var response = todoListService.updateTodoList(userId, listId, request);

        assertThat(response.title()).isEqualTo("New title");
    }

    @Test
    void updateTodoList_whenListDoesNotBelongToUser_throwsNotFound() {
        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();

        when(todoListRepository.findByIdAndUserId(listId, userId))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoListService.updateTodoList(
            userId,
            listId,
            new UpdateTodoListRequest("New title")
        ))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Todo list not found with id");

        verify(todoListRepository, never()).save(any());
    }

    @Test
    void getTodoList_returnsListWithOnlyNotDoneTasksWhenItBelongsToUser() {
        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();

        TodoList list = new TodoList(userId, "Project tasks");

        TodoTask task = new TodoTask(
            list.id(),
            userId,
            "Task 1",
            TodoPriority.HIGH,
            LocalDateTime.now().plusDays(1)
        );

        when(todoListRepository.findByIdAndUserId(listId, userId))
            .thenReturn(Optional.of(list));

        when(todoTaskRepository.findAllByListIdAndDoneFalse(list.id()))
            .thenReturn(List.of(task));

        var response = todoListService.getTodoList(userId, listId);

        assertThat(response.title()).isEqualTo("Project tasks");
        assertThat(response.tasks()).hasSize(1);
        assertThat(response.tasks().getFirst().title()).isEqualTo("Task 1");
    }

    @Test
    void getTodoList_whenListDoesNotBelongToUser_throwsNotFound() {
        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();

        when(todoListRepository.findByIdAndUserId(listId, userId))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoListService.getTodoList(userId, listId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Todo list not found with id");

        verify(todoTaskRepository, never()).findAllByListIdAndDoneFalse(any());
    }

    @Test
    void deleteTodoList_detachesTasksAndDeletesListWhenItBelongsToUser() {
        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();

        TodoList list = new TodoList(userId, "Project tasks");

        when(todoListRepository.findByIdAndUserId(listId, userId))
            .thenReturn(Optional.of(list));

        todoListService.deleteTodoList(userId, listId);

        verify(todoTaskRepository).detachAllByListId(list.id());
        verify(todoListRepository).delete(list);
    }

    @Test
    void deleteTodoList_whenListDoesNotBelongToUser_throwsNotFound() {
        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();

        when(todoListRepository.findByIdAndUserId(listId, userId))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoListService.deleteTodoList(userId, listId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Todo list not found with id");

        verify(todoTaskRepository, never()).detachAllByListId(any());
        verify(todoListRepository, never()).delete(any());
    }

    @Test
    void getTodoListsWithTasks_returnsListsWithTheirTasks() {
        UUID userId = UUID.randomUUID();
        UUID firstListId = UUID.randomUUID();
        UUID secondListId = UUID.randomUUID();

        TodoList firstList = todoListWithId(firstListId, userId, "University");
        TodoList secondList = todoListWithId(secondListId, userId, "Work");

        TodoTask firstTask = new TodoTask(
            firstListId,
            userId,
            "Study Spring",
            TodoPriority.HIGH,
            LocalDateTime.now().plusDays(1)
        );

        TodoTask secondTask = new TodoTask(
            secondListId,
            userId,
            "Prepare meeting",
            TodoPriority.MEDIUM,
            LocalDateTime.now().plusDays(2)
        );

        when(todoListRepository.findAllByUserId(userId))
            .thenReturn(List.of(firstList, secondList));

        when(todoTaskRepository.findAllByUserIdAndDoneFalseAndListIdNotNull(userId))
            .thenReturn(List.of(firstTask, secondTask));

        var response = todoListService.getTodoListsWithTasks(userId);

        assertThat(response).hasSize(2);

        assertThat(response.getFirst().title()).isEqualTo("University");
        assertThat(response.getFirst().tasks()).hasSize(1);
        assertThat(response.getFirst().tasks().getFirst().title()).isEqualTo("Study Spring");

        assertThat(response.get(1).title()).isEqualTo("Work");
        assertThat(response.get(1).tasks()).hasSize(1);
        assertThat(response.get(1).tasks().getFirst().title()).isEqualTo("Prepare meeting");
    }

    private TodoList todoListWithId(UUID id, UUID userId, String title) {
        TodoList list = new TodoList(userId, title);
        ReflectionTestUtils.setField(list, "id", id);
        return list;
    }
}