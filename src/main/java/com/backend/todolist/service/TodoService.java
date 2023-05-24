package com.backend.todolist.service;

import com.backend.todolist.controller.CountResponse;
import com.backend.todolist.controller.TodoCreateRequest;
import com.backend.todolist.controller.TodoUpdateRequest;
import com.backend.todolist.errorhandler.ResourceNotFoundException;
import com.backend.todolist.model.Todo;
import com.backend.todolist.repository.TodoPagingRepository;
import com.backend.todolist.repository.TodoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {
    private final TodoRepository todoRepository;
    private final TodoPagingRepository todoPagingRepository;

    public TodoService(TodoRepository todoRepository, TodoPagingRepository todoPagingRepository) {
        this.todoRepository = todoRepository;
        this.todoPagingRepository = todoPagingRepository;
    }

    public Todo create(TodoCreateRequest todoCreateRequest, String username) {
        Todo todo = new Todo(todoCreateRequest.getTitle(), todoCreateRequest.getTargetDate(), username);
        return todoRepository.save(todo);
    }

    public Todo readById(long id, String username) {
        Todo todo = todoRepository.findByUsernameAndId(username, id);
        if (todo == null) {
            throw new ResourceNotFoundException("Todo not found");
        }
        return todo;
    }

    public List<Todo> readAll(String username) {
        return todoRepository.findAllByUsername(username);
    }

    public List<Todo> readAllPageable(String username, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "targetDate"));
        return todoPagingRepository.findAllByUsername(username, pageable);
    }

    public List<Todo> readAllByIsCompleted(String username, Boolean isCompleted) {
        return todoRepository.findAllByUsernameAndIsCompleted(username, isCompleted);
    }

    public List<Todo> readAllByIsCompletedPageable(String username, boolean isCompleted, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "targetDate"));
        return todoPagingRepository.findAllByUsernameAndIsCompleted(username, isCompleted, pageable);
    }

    public void deleteById(long id, String username) {
        Todo todo = todoRepository.findByUsernameAndId(username, id);
        if (todo == null) {
            throw new ResourceNotFoundException("Todo not found");
        }
        todoRepository.deleteById(id);
    }

    public Todo updateById(long id, TodoUpdateRequest todoUpdateRequest, String username) {
        Todo todo = todoRepository.findByUsernameAndId(username, id);
        if (todo == null) {
            throw new ResourceNotFoundException("Todo not found");
        }

        todo.setTitle(todoUpdateRequest.getTitle());
        todo.setTargetDate(todoUpdateRequest.getTargetDate());
        return todoRepository.save(todo);
    }

    public Todo markCompleteById(long id, String username) {
        Todo todo = todoRepository.findByUsernameAndId(username, id);
        if (todo == null) {
            throw new ResourceNotFoundException("Todo not found");
        }

        todo.setIsCompleted(!todo.getIsCompleted());
        return todoRepository.save(todo);
    }

    public CountResponse countAll(String username) {
        return new CountResponse(todoRepository.countByUsername(username));
    }

    public CountResponse countAllByIsCompleted(String username, boolean isCompleted) {
        return new CountResponse(todoRepository.countByUsernameAndIsCompleted(username, isCompleted));
    }
}
