package com.javarush.entity.cache;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import javax.persistence.*;

@Entity
@Table(name = "cache_city")
@Data
@NoArgsConstructor
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String countryCode;
}