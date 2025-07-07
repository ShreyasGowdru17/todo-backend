package com.raksh.ToDo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.util.List;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    @Email
    @NotBlank
    private String userName;
    private String password;
    private boolean enabled=false;

    @OneToMany(
            mappedBy="user",
            cascade=CascadeType.ALL
    )
    private List<ToDo> toDoList;


}
