package org.example.springtask.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "task_name")
    private String taskName;


    @Column(name = "date_create_task")
    private LocalDateTime dateCreateTask;

    @Column(name = "user_id")
    private Integer userId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) &&
                Objects.equals(taskName, task.taskName) &&
                Objects.equals(dateCreateTask, task.dateCreateTask) &&
                Objects.equals(userId, task.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskName, dateCreateTask, userId);
    }
}
