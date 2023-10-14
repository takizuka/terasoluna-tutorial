package com.example.todo.app.todo;

import com.example.todo.domain.model.todo.Todo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TodoMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "finished", ignore = true)
    Todo map(TodoForm form);
}
