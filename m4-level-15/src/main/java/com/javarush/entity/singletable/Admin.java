package com.javarush.entity.singletable;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@DiscriminatorValue("ADMIN")
@Getter
@Setter
public class Admin extends Person {
    @Column(name = "security_level")
    private Integer securityLevel;

    @Column(name = "can_delete_users")
    private Boolean canDeleteUsers = false;

    @Column(name = "super_admin")
    private Boolean superAdmin = false;
}