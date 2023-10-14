package com.example.todo.domain.service.todo;

import com.example.todo.domain.model.todo.Todo;

import java.util.Collection;

public interface TodoService {
    Collection<Todo> findAll();

    Todo create(Todo todo);

    Todo finish(String todoId);

    void delete(String todoId);
}
