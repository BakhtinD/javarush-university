package com.javarush.entity.cache;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cache_booking")
@Data
@NoArgsConstructor
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String guestName;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;
}