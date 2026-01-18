package com.javarush.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "table_items")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class TableItem {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "table_gen")
    @TableGenerator(name = "table_gen", table = "id_generator",
            pkColumnName = "gen_name", valueColumnName = "gen_value",
            pkColumnValue = "item_id", allocationSize = 1)
    private Long id;

    @Column(name = "name")
    private String name;

    public TableItem(String name) {
        this.name = name;
    }
}