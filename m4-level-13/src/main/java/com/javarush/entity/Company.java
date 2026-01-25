package com.javarush.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Embedded
    private Address address;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "email", column = @Column(name = "contact_email")),
            @AttributeOverride(name = "phone", column = @Column(name = "contact_phone")),
            @AttributeOverride(name = "website", column = @Column(name = "company_website"))
    })
    private ContactInfo contactInfo;

    public Company(String name, Address address, ContactInfo contactInfo) {
        this.name = name;
        this.address = address;
        this.contactInfo = contactInfo;
    }
}