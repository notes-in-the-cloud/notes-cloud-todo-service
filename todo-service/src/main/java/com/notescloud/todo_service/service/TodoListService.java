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

    public TodoListResponse createTodoList(CreateTodoListRequest request) {
        TodoList todoList = new TodoList(request.userId(), request.title());
        todoListRepository.save(todoList);
        return TodoListResponse.from(todoList);
    }

    @Transactional
    public void deleteTodoList(UUID id) {
        if (!todoListRepository.existsById(id)) {
            throw new ResourceNotFoundException("Todo list not found with id: " + id);
        }
        todoTaskRepository.detachAllByListId(id);
        todoListRepository.deleteById(id);
    }

    public TodoListResponse updateTodoList(UUID id, UpdateTodoListRequest request) {
        TodoList todoList = todoListRepository.findById(id).orElseThrow(()
            -> new ResourceNotFoundException("Todo list not found with id: " + id));
        todoList.updateTitle(request.title());
        TodoList saved = todoListRepository.save(todoList);
        return TodoListResponse.from(saved);
    }

    public TodoListWithTasksResponse getTodoList(UUID id) {
        TodoList list = todoListRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Todo list not found with id: " + id));
        List<TodoTask> tasks = todoTaskRepository.findAllByListIdAndDoneFalse(list.id());
        return TodoListWithTasksResponse.from(list, tasks);
    }

    public List<TodoListWithTasksResponse> getTodoListsWithTasks(UUID userId) {
        List<TodoList> lists = todoListRepository.findAllByUserId(userId);
        List<TodoTask> tasks = todoTaskRepository.findAllByUserIdAndDoneFalseAndListIdNotNull(userId);
        return lists.stream()
            .map(list -> {
                List<TodoTask> listTasks = tasks.stream()
                    .filter(task -> task.listId().equals(list.id()))
                    .toList();
                return TodoListWithTasksResponse.from(list, listTasks);
            }).toList();
    }
}
