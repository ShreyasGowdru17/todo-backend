package com.raksh.ToDo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ToDo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;
    private String taskName;
    private boolean taskCompleted;
    @CreationTimestamp
    private java.time.Instant createdAt;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(
            name="user_id",
            referencedColumnName = "id"
    )
    private User user;
}
