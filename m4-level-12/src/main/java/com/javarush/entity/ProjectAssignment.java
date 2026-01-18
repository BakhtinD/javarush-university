package com.javarush.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "project_assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProjectAssignment {

    // Составной ключ с @EmbeddedId
    @EmbeddedId
    private AssignmentId id;

    @Column(name = "role")
    private String role;

    @Column(name = "start_date")
    private LocalDate startDate;

    public ProjectAssignment(String projectCode, Integer employeeId,
                             String role, LocalDate startDate) {
        this.id = new AssignmentId(projectCode, employeeId);
        this.role = role;
        this.startDate = startDate;
    }
}