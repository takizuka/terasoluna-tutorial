package com.example.todo.domain.repository.todo;

import com.example.todo.domain.model.todo.Todo;

import java.util.Collection;
import java.util.Optional;

public interface TodoRepository {
    Optional<Todo> findById(String todoId);

    Collection<Todo> findAll();

    void create(Todo todo);

    boolean update(Todo todo);

    void delete(Todo todo);

    long countByFinished(boolean finished);
}
