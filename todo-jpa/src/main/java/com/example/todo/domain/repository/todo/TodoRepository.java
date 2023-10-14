package com.example.todo.domain.repository.todo;

import com.example.todo.domain.model.todo.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TodoRepository extends JpaRepository<Todo, String> {

    @Query("SELECT COUNT(t) FROM Todo t WHERE t.finished = :finished")
    long countByFinished(@Param("finished") boolean finished);
}
