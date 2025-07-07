package com.raksh.ToDo.service;

import com.raksh.ToDo.model.ToDo;
import com.raksh.ToDo.model.User;
import com.raksh.ToDo.repository.ToDoRepository;
import com.raksh.ToDo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToDoServiceImplementation implements ToDoService{

    @Autowired
    private ToDoRepository toDoRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ToDo addTask(String task,String userName) {
        User user=userRepository.findByUserName(userName);
        ToDo toDo=new ToDo();
        toDo.setTaskName(task);
        toDo.setTaskCompleted(false);
        toDo.setUser(user);
        return toDoRepository.save(toDo);
    }

    @Override
    public List<ToDo> getAllTask(String userName) {
        List<ToDo> task=toDoRepository.findAllByUser_UserName(userName);
        return task;
    }

    @Override
    public void deleteTaskById(Long id,String userName) {
        ToDo toDo=toDoRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("No task with id:"+id));

        if(!toDo.getUser().getUserName().equals(userName)){
            throw new SecurityException("Not Allowed to delete task not owned by you");
        }

        toDoRepository.deleteById(id);
    }

    @Override
    public String toggleTaskById(Long id,String userName) {
        ToDo toDo= toDoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No task " + id));

        if(!toDo.getUser().getUserName().equals(userName)){
            throw new SecurityException("Not Allowed to delete task not owned by you");
        }

        boolean now = ! toDo.isTaskCompleted();
        toDo.setTaskCompleted(now);
        toDoRepository.save(toDo);              // ← actually persist the change

        return now
                ? "Task marked completed"
                : "Task unmarked";
    }


}
