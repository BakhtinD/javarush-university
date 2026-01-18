package com.javarush.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tasks")  // Изменено с "task" для единообразия
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmployeeTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    // Связь ManyToOne - много задач могут быть у одного сотрудника
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = true) // nullable = true - задача может быть без сотрудника
    private Employee employee;

    @Column(name = "deadline")
    @Temporal(TemporalType.TIMESTAMP) // Указываем тип даты в БД
    private Date deadline;

    @Column(name = "status")
    private String status;

    // Конструктор без ID для удобства
    public EmployeeTask(String name, Employee employee, Date deadline, String status) {
        this.name = name;
        this.employee = employee;
        this.deadline = deadline;
        this.status = status;
    }
}