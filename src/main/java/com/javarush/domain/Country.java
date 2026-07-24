package com.javarush.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "country", schema = "world")
@BatchSize(size=500)
public class Country {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    @BatchSize(size=500)
    @JsonManagedReference
    private Set<CountryLanguage> languages;
    @Column(name = "code_2", nullable = false, length = 2)
    private String alternativeCode;
    @Column(name = "continent", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Continent continent;
    @Column(name = "surface_area", nullable = false, precision = 10, scale = 2)
    private BigDecimal surfaceArea;
    @Column(name = "indep_year")
    private Short independenceYear;
    @Column(name = "population", nullable = false)
    private Integer population;
    @Column(name = "life_expectancy", precision = 3, scale = 1)
    private BigDecimal lifeExpectancy;
    @Column(name = "gnp", precision = 10, scale = 2)
    private BigDecimal gnp;
    @Column(name = "gnpo_id", precision = 10, scale = 2)
    private BigDecimal GNPOId;
    @Column(name = "local_name", nullable = false, length = 45)
    private String localName;
    @Column(name = "government_form", nullable = false, length = 45)
    private String governmentForm;
    @Column(name = "head_of_state", length = 60)
    private String headOfState;
    @OneToOne()
    @JoinColumn(name = "capital")
    @JsonManagedReference
    private City capital;
    @Column(name = "code", nullable = false, length = 3)
    private String code;
    @Column(name = "name", nullable = false, length = 52)
    private String name;
    @Column(name = "region", nullable = false, length = 26)
    private String region;

    @Override
    public String toString() {
        return "Country{" + "code='" + code + '\'' + ", id=" + id
                + ", alternativeCode='" + alternativeCode + '\'' + ", name='" + name + '\''
                + ", region='" + region + '\'' + ", surfaceArea=" + surfaceArea
                + ", independenceYear=" + independenceYear + ", population=" + population + ", lifeExpectancy=" + lifeExpectancy + ", gnp=" + gnp + ", GNPOId=" + GNPOId + ", localName='" + localName + '\'' + ", headOfState='" + headOfState + '\'' + ", governmentForm='" + governmentForm + '\'' + '}';
    }

}