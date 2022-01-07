package org.example.springtask.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
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
   /* @JoinTable(name = "user_project",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "project_id")}
HashSet<>()
    )*/
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Worker worker = (Worker) o;
        return Objects.equals(id, worker.id) &&
                Objects.equals(firstName, worker.firstName) &&
                Objects.equals(lastName, worker.lastName) &&
                Objects.equals(projects, worker.projects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, projects);
    }

}
