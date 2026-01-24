package com.javarush.entity.singletable;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@DiscriminatorValue("REGULAR")
@Getter
@Setter
public class RegularUser extends Person {
    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "last_login")
    private java.time.LocalDateTime lastLogin;
}