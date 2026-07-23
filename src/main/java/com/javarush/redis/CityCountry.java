package com.javarush.redis;

import com.javarush.domain.Continent;
import com.javarush.domain.CountryLanguage;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class CityCountry {

        private String district;

        private Integer population;

        private Integer id;

        private Set<CountryLanguage> languages;

        private String alternativeCode;

        private String name;

        private Continent continent;

        private String region;

        private BigDecimal surfaceArea;

        private Short independenceYear;

        private BigDecimal lifeExpectancy;

        private BigDecimal gnp;

        private BigDecimal GNPOId;

        private String localName;

        private String governmentForm;

        private String headOfState;

        private String capital;

        private String code;

    }