package com.javarush.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "sequence_items")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class SequenceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_gen")
    @SequenceGenerator(name = "seq_gen", sequenceName = "item_sequence", allocationSize = 1)
    private Long id;

    @Column(name = "name")
    private String name;

    public SequenceItem(String name) {
        this.name = name;
    }
}