package com.javarush.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Table(name = "country_language", schema = "world")
public class CountryLanguage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "country_id", nullable = false)
    @JsonBackReference
    private Country country;
    @Column(name = "is_official", columnDefinition = "BIT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean isOfficial;

    @Column(name = "language", nullable = false, length = 30)
    private String language;
    @Column(name = "percentage", nullable = false, precision = 4, scale = 1)
    private BigDecimal percentage;

    @Override
    public String toString() {
        return "CountryLanguage{" + "id=" + id + ", language='" + language + '\'' + ", isOfficial=" + isOfficial + ", percentage=" + percentage + '}';
    }


}