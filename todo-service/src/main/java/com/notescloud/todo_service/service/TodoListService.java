package com.notescloud.todo_service.service;

import com.notescloud.todo_service.domain.TodoList;
import com.notescloud.todo_service.domain.TodoTask;
import com.notescloud.todo_service.dto.CreateTodoListRequest;
import com.notescloud.todo_service.dto.TodoListResponse;
import com.notescloud.todo_service.dto.TodoListWithTasksResponse;
import com.notescloud.todo_service.dto.UpdateTodoListRequest;
import com.notescloud.todo_service.exception.ResourceNotFoundException;
import com.notescloud.todo_service.repository.TodoListRepository;
import com.notescloud.todo_service.repository.TodoTaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TodoListService {
    private final TodoListRepository todoListRepository;
    private final TodoTaskRepository todoTaskRepository;

    public TodoListService(TodoListRepository todoListRepository,
                           TodoTaskRepository todoTaskRepository) {
        this.todoListRepository = todoListRepository;
        this.todoTaskRepository = todoTaskRepository;
    }

    public TodoListResponse createTodoList(UUID userId, CreateTodoListRequest request) {
        TodoList todoList = new TodoList(userId, request.title());
        TodoList savedList = todoListRepository.save(todoList);
        return TodoListResponse.from(savedList);
    }

    @Transactional
    public void deleteTodoList(UUID userId, UUID listId) {
        TodoList list = getListForUser(userId, listId);

        todoTaskRepository.detachAllByListId(list.id());
        todoListRepository.delete(list);
    }

    public TodoListResponse updateTodoList(UUID userId, UUID listId, UpdateTodoListRequest request) {
        TodoList todoList = getListForUser(userId, listId);
        todoList.updateTitle(request.title());
        TodoList savedList = todoListRepository.save(todoList);
        return TodoListResponse.from(savedList);
    }

    public TodoListWithTasksResponse getTodoList(UUID userId, UUID listId) {
        TodoList list = getListForUser(userId, listId);

        List<TodoTask> tasks = todoTaskRepository.findAllByListIdAndDoneFalse(list.id());

        return TodoListWithTasksResponse.from(list, tasks);
    }

    public List<TodoListWithTasksResponse> getTodoListsWithTasks(UUID userId) {
        List<TodoList> lists = todoListRepository.findAllByUserId(userId);
        List<TodoTask> tasks = todoTaskRepository.findAllByUserIdAndDoneFalseAndListIdNotNull(userId);

        return lists.stream()
            .map(list -> {
                List<TodoTask> listTasks = tasks.stream()
                    .filter(task -> list.id().equals(task.listId()))
                    .toList();

                return TodoListWithTasksResponse.from(list, listTasks);
            })
            .toList();
    }

    private TodoList getListForUser(UUID userId, UUID listId) {
        return todoListRepository.findByIdAndUserId(listId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Todo list not found with id: " + listId));
    }
}