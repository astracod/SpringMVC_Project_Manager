package org.example.springtask.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "File")
@Table(name = "files")
@Getter
@Setter
public class File {

    @Id
    private Integer id;


    @Column(name = "path_to_file")
    private String pathToFile;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Task task;

    public File() {
    }

    public File(String pathToFile) {
        this.pathToFile = pathToFile;
    }
}
