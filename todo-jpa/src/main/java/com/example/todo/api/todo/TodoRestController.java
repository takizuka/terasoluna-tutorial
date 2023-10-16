package com.example.todo.api.todo;

import com.example.todo.domain.service.todo.TodoService;
import jakarta.inject.Inject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("todos")
public class TodoRestController {
    @Inject
    TodoService todoService;

    @Inject
    TodoMapper beanMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TodoResource> getTodos() {
        return todoService.findAll().stream().map(todo -> beanMapper.map(todo)).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoResource postTodos(@RequestBody @Validated TodoResource todoResource) {
        var createdTodo = todoService.create(beanMapper.map(todoResource));

        return beanMapper.map(createdTodo);
    }

    @GetMapping("{todoId}")
    @ResponseStatus(HttpStatus.OK)
    public TodoResource getTodo(@PathVariable("todoId") String todoId) {
        var todo = todoService.findOne(todoId);

        return beanMapper.map(todo);
    }

    @PutMapping("{todoId}")
    @ResponseStatus(HttpStatus.OK)
    public TodoResource putTodo(@PathVariable("todoId") String todoId) {
        var finishedTodo = todoService.finish(todoId);

        return beanMapper.map(finishedTodo);
    }

    @DeleteMapping("{todoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodo(@PathVariable("todoId") String todoId) {
        todoService.delete(todoId);
    }
}
