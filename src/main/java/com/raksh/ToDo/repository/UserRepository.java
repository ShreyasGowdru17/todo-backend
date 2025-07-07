package com.raksh.ToDo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.raksh.ToDo.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByUserName(String username);

}
