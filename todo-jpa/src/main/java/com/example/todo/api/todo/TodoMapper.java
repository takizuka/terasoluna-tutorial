package com.example.todo.api.todo;

import com.example.todo.domain.model.todo.Todo;
import org.mapstruct.Mapper;

@Mapper
public interface TodoMapper {
    TodoResource map(Todo todo);

    Todo map(TodoResource todoResource);
}
