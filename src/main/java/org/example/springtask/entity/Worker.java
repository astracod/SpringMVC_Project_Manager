package org.example.springtask.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity(name = "Worker")
@Table(name = "users")
public class Worker {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "email")
    private String login;
    @Column(name = "password")
    private String password;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @ManyToMany(mappedBy = "workers")
    @JsonIgnore
    private Set<Project> projects = new ConcurrentSkipListSet<>();

    public void addProject(Project project) {
        this.projects.add(project);
        project.getWorkers().add(this);
    }

    public void removeProject(Project project) {
        this.projects.remove(project);
        project.getWorkers().remove(this);
    }
}
