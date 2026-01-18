package com.javarush.entity;

import lombok.*;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AssignmentId implements Serializable {
    private String projectCode;
    private Integer employeeId;
}