package com.javarush.entity.singletable;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@DiscriminatorValue("EMPLOYEE")
@Getter
@Setter
public class Employee extends Person {
    @Column(name = "employee_id", unique = true)
    private String employeeId;

    @Column(name = "department")
    private String department;

    @Column(name = "salary")
    private Double salary;
}