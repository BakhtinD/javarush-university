package com.javarush.redis;

import com.javarush.domain.Country;
import lombok.Data;

@Data
public class CityDetail {

    private Integer id;

    private Country country;

    private String name;

    private String district;
    
    private Integer population;
}