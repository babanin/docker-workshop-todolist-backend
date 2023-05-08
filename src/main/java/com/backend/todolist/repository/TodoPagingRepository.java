package com.backend.todolist.repository;

import com.backend.todolist.model.Todo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoPagingRepository extends PagingAndSortingRepository<Todo, Long> {
    List<Todo> findAllByUsername(String username, Pageable pageable);

    List<Todo> findAllByUsernameAndIsCompleted(String username, boolean isCompleted, Pageable pageable);
}
