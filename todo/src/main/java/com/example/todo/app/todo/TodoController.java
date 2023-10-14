package com.example.todo.app.todo;

import com.example.todo.domain.service.todo.TodoService;
import jakarta.inject.Inject;
import jakarta.validation.groups.Default;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.terasoluna.gfw.common.exception.BusinessException;
import org.terasoluna.gfw.common.message.ResultMessage;
import org.terasoluna.gfw.common.message.ResultMessages;

@Controller
@RequestMapping("todo")
public class TodoController {
    @Inject
    TodoService todoService;

    @Inject
    TodoMapper beanMapper;

    @ModelAttribute
    public TodoForm setUpForm() {
        return new TodoForm();
    }

    @GetMapping("list")
    public String list(Model model) {
        var todos = todoService.findAll();
        model.addAttribute("todos", todos);

        return "todo/list";
    }

    @PostMapping("create")
    public String create(
            @Validated({Default.class, TodoForm.TodoCreate.class}) TodoForm todoForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes attributes
    ) {
        if (bindingResult.hasErrors()) {
            return list(model);
        }

        var todo = beanMapper.map(todoForm);

        try {
            todoService.create(todo);
        } catch (BusinessException e) {
            model.addAttribute(e.getResultMessages());
            return list(model);
        }

        var messages = ResultMessages.success();
        messages.add(ResultMessage.fromText("Created successfully!"));
        attributes.addFlashAttribute(messages);

        return "redirect:/todo/list";
    }

    @PostMapping("finish")
    public String finish(
            @Validated({Default.class, TodoForm.TodoFinish.class}) TodoForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes attributes
    ) {
        if (bindingResult.hasErrors()) {
            return list(model);
        }

        try {
            todoService.finish(form.getTodoId());
        } catch (BusinessException e) {
            model.addAttribute(e.getResultMessages());
            return list(model);
        }

        var messages = ResultMessages.success();
        messages.add(ResultMessage.fromText("Finished successfully!"));
        attributes.addFlashAttribute(messages);

        return "redirect:/todo/list";
    }

    @PostMapping("delete")
    public String delete(
            @Validated({Default.class, TodoForm.TodoDelete.class}) TodoForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes attributes
    ) {
        if (bindingResult.hasErrors()) {
            return list(model);
        }

        try {
            todoService.delete(form.getTodoId());
        } catch (BusinessException e) {
            model.addAttribute(e.getResultMessages());
            return list(model);
        }

        var messages = ResultMessages.success();
        messages.add(ResultMessage.fromText("Deleted successfully!"));
        attributes.addFlashAttribute(messages);

        return "redirect:/todo/list";
    }
}
