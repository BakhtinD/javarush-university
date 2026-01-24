package com.javarush.entity.cache;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cache_hotel")
@Data
@NoArgsConstructor
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal pricePerNight;

    private Integer rating;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;
}