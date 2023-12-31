package com.example.todo.domain.service.todo;

import com.example.todo.domain.model.todo.Todo;
import com.example.todo.domain.repository.todo.TodoRepository;
import jakarta.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terasoluna.gfw.common.exception.BusinessException;
import org.terasoluna.gfw.common.exception.ResourceNotFoundException;
import org.terasoluna.gfw.common.message.ResultMessages;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@Service
@Transactional
public class TodoServiceImpl implements TodoService {
    private static final long MAX_UNFINISHED_COUNT = 5;

    @Inject
    TodoRepository todoRepository;

    @Override
    @Transactional(readOnly = true)
    public Todo findOne(String todoId) {
        return todoRepository.findById(todoId).orElseThrow(() -> {
            var messages = ResultMessages.error();
            messages.add("E404", todoId);
            return new ResourceNotFoundException(messages);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Todo> findAll() {
        return todoRepository.findAll();
    }

    @Override
    public Todo create(Todo todo) {
        long unfinishedCount = todoRepository.countByFinished(false);
        if (unfinishedCount >= MAX_UNFINISHED_COUNT) {
            var messages = ResultMessages.error();
            messages.add("E001", MAX_UNFINISHED_COUNT);
            throw new BusinessException(messages);
        }

        todo.setTodoId(UUID.randomUUID().toString());
        todo.setCreatedAt(new Date());
        todo.setFinished(false);

        todoRepository.save(todo);

        return todo;
    }

    @Override
    public Todo finish(String todoId) {
        var todo = findOne(todoId);
        if (todo.isFinished()) {
            ResultMessages messages = ResultMessages.error();
            messages.add("E002", todoId);
            throw new BusinessException(messages);
        }

        todo.setFinished(true);
        todoRepository.save(todo);

        return todo;
    }

    @Override
    public void delete(String todoId) {
        var todo = findOne(todoId);
        todoRepository.delete(todo);
    }
}
