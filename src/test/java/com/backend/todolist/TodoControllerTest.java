package com.backend.todolist;

import com.backend.todolist.auth.jwt.JwtTokenGenerator;
import com.backend.todolist.controller.TodoController;
import com.backend.todolist.model.Todo;
import com.backend.todolist.service.TodoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TodoController.class)
class TodoControllerTest {
    @MockBean
    private TodoService todoService;

    @MockBean
    private JwtTokenGenerator jwtTokenGenerator;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(value = "todo-user")
    public void shouldReturnEmptyArray() throws Exception {
        this.mockMvc.perform(get("/api/todo"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
    
    @Test
    @WithMockUser(value = "todo-user")
    public void shouldReturnListOfTodos() throws Exception {
        when(todoService.readAll("todo-user"))
                .thenReturn(List.of(new Todo("test", new Date(1234567890123L), "todo-user")));

        this.mockMvc.perform(get("/api/todo"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{"id":0,"title":"test","targetDate":"2009-02-13T23:31:30.123+00:00","username":"todo-user","isCompleted":false}]
                        """));
    }

}
