package org.example.springtask.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Project")
@Table(name = "projects")
public class Project {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "project_name")
    private String projectName;

    //(mappedBy = "projects")
    @ManyToMany(
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            }
    )
    @JoinTable(name = "user_project",
            joinColumns = {@JoinColumn(name = "project_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private Set<Worker> workers = new HashSet<>();


    @OneToMany
    @JoinColumn(name = "id")
    private Set<Task> tasks = new HashSet<>();

    public void addTask(Task task) {
        this.tasks.add(task);
        task.setProject(this);
    }

    public void removeTask(Task task){
        this.tasks.remove(task);
        task.setProject(this);
    }

    public void addWorker(Worker worker) {
            this.workers.add(worker);
            worker.getProjects().add(this);
    }

    public void removeWorker(Worker worker) {
            this.workers.remove(worker);
            worker.getProjects().remove(this);
    }

}
