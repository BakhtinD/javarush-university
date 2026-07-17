package com.javarush.redis;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class CountryDetail {

        private Integer id;

        private String code;

        private Set<String> languages;

        private String alternativeCode;

        private String name;

        private String continent;

        private String region;

        private BigDecimal surfaceArea;

        private Short independenceYear;

        private Integer population;

        private BigDecimal lifeExpectancy;

        private BigDecimal gnp;

        private BigDecimal GNPOId;

        private String localName;

        private String governmentForm;

        private String headOfState;

        private String capital;


    }