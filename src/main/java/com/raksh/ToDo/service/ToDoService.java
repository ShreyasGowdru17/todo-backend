package com.raksh.ToDo.service;

import com.raksh.ToDo.model.ToDo;

import java.util.List;

public interface ToDoService {
    ToDo addTask(String task,String userName);

    List<ToDo> getAllTask(String userName);

    void deleteTaskById(Long id,String userName);

    String toggleTaskById(Long id,String userName);
}
