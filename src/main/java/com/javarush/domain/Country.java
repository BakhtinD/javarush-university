package com.javarush.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "country", schema = "world")
@BatchSize(size=500)
public class Country {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
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
    @OneToOne
    @JoinColumn(name = "capital")
    private City capital;
    @Column(name = "code", nullable = false, length = 3)
    private String code;
    @Column(name = "name", nullable = false, length = 52)
    private String name;
    @Column(name = "region", nullable = false, length = 26)
    private String region;

    @Override
    public String toString() {
        return "Country{" + "code='" + code + '\'' + ", id=" + id + ", alternativeCode='" + alternativeCode + '\'' + ", name='" + name + '\'' + ", region='" + region + '\'' + ", surfaceArea=" + surfaceArea + ", independenceYear=" + independenceYear + ", population=" + population + ", lifeExpectancy=" + lifeExpectancy + ", gnp=" + gnp + ", GNPOId=" + GNPOId + ", localName='" + localName + '\'' + ", headOfState='" + headOfState + '\'' + ", governmentForm='" + governmentForm + '\'' + '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAlternativeCode() {
        return alternativeCode;
    }

    public void setAlternativeCode(String alternative_code) {
        this.alternativeCode = alternative_code;
    }

    public Set<CountryLanguage> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<CountryLanguage> languages) {
        this.languages = languages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Continent getContinent() {
        return continent;
    }

    public void setContinent(Continent continent) {
        this.continent = continent;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public BigDecimal getSurfaceArea() {
        return surfaceArea;
    }

    public void setSurfaceArea(BigDecimal surfaceArea) {
        this.surfaceArea = surfaceArea;
    }

    public Short getIndependenceYear() {
        return independenceYear;
    }

    public void setIndependenceYear(Short indepYear) {
        this.independenceYear = indepYear;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public BigDecimal getLifeExpectancy() {
        return lifeExpectancy;
    }

    public void setLifeExpectancy(BigDecimal lifeExpectancy) {
        this.lifeExpectancy = lifeExpectancy;
    }

    public BigDecimal getGnp() {
        return gnp;
    }

    public void setGnp(BigDecimal gnp) {
        this.gnp = gnp;
    }

    public BigDecimal getGNPOId() {
        return GNPOId;
    }

    public void setGNPOId(BigDecimal gnpoId) {
        this.GNPOId = gnpoId;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getGovernmentForm() {
        return governmentForm;
    }

    public void setGovernmentForm(String governmentForm) {
        this.governmentForm = governmentForm;
    }

    public String getHeadOfState() {
        return headOfState;
    }

    public void setHeadOfState(String headOfState) {
        this.headOfState = headOfState;
    }

    public City getCapital() {
        return capital;
    }

    public void setCapital(City capital) {
        this.capital = capital;
    }

}