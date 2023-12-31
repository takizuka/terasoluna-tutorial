package com.example.todo.domain.repository.todo;

import com.example.todo.domain.model.todo.Todo;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TodoRepositoryImpl implements TodoRepository {
    private static final Map<String, Todo> TODO_MAP = new ConcurrentHashMap<>();

    @Override
    public Optional<Todo> findById(String todoId) {
        return Optional.ofNullable(TODO_MAP.get(todoId));
    }

    @Override
    public Collection<Todo> findAll() {
        return TODO_MAP.values();
    }

    @Override
    public void create(Todo todo) {
        TODO_MAP.put(todo.getTodoId(), todo);
    }

    @Override
    public boolean update(Todo todo) {
        TODO_MAP.put(todo.getTodoId(), todo);
        return true;
    }

    @Override
    public void delete(Todo todo) {
        TODO_MAP.remove(todo.getTodoId());

    }

    @Override
    public long countByFinished(boolean finished) {
        return TODO_MAP.values().stream().filter(todo -> todo.isFinished() == finished).count();
    }

}
