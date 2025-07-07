package com.raksh.ToDo.controller;

import com.raksh.ToDo.model.ToDo;
import com.raksh.ToDo.service.ToDoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/todo")
public class ToDoController {

    @Autowired
    private ToDoService toDoService;

    @PostMapping("addTask")
    public ResponseEntity<ToDo> addTask(@RequestBody Map<String, String> request, Principal principal) {
        String task = request.get("task");
        String username = principal.getName(); // From JWT
        ToDo newTask = toDoService.addTask(task, username);
        return ResponseEntity.ok(newTask);
    }


    @GetMapping("/allTask")
    public List<ToDo> getAllTask(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String userName=authentication.getName();
        List<ToDo> task=toDoService.getAllTask(userName);
        return task;
    }

    @DeleteMapping("/deleteTask/{id}")
    public String deleteTaskById(@PathVariable("id") Long id){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String userName=authentication.getName();
        toDoService.deleteTaskById(id,userName);
        return "Task Deleted Successfully";
    }

    @PutMapping("/toggleTask/{id}")
    public String toggleTaskById(@PathVariable("id") Long id){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String userName=authentication.getName();
        return toDoService.toggleTaskById(id,userName);
    }
}
