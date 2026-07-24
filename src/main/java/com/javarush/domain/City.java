package com.javarush.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Setter
@Getter
@Entity
@Table(name = "city", schema = "world")
@BatchSize(size=500)
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "country_id", nullable = false)
    @JsonBackReference
    private Country country;
    @Column(name = "name", nullable = false, length = 35)
    private String name;
    @Column(name = "district", nullable = false, length = 20)
    private String district;
    @Column(name = "population", nullable = false)
    private Integer population;

    @Override
    public String toString() {
        return "City{" + "id=" + id + ", name='" + name + '\'' + ", city_district='" + district + '\'' + ", population=" + population + '}';
    }

}