package com.raksh.ToDo.repository;

import com.raksh.ToDo.model.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToDoRepository extends JpaRepository<ToDo,Long> {

    List<ToDo> findAllByUser_UserName(String userName);
}
