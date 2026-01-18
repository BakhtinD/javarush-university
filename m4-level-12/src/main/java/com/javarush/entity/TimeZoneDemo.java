package com.javarush.entity;

import lombok.*;

import javax.persistence.*;
import java.time.*;
import java.util.Date;

@Entity
@Table(name = "timezone_demo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TimeZoneDemo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    // Дата без времени (не зависит от пояса)
    @Column(name = "local_date")
    private LocalDate localDate;

    // Дата-время без пояса (как в календаре)
    @Column(name = "local_date_time")
    private LocalDateTime localDateTime;

    // Момент времени (всегда UTC)
    @Column(name = "instant")
    private Instant instant;

    // Дата-время со смещением
    @Column(name = "offset_date_time")
    private OffsetDateTime offsetDateTime;

    // Старый Date (зависит от JVM timezone)
    @Column(name = "util_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date utilDate;

    public TimeZoneDemo(String name, LocalDate localDate, LocalDateTime localDateTime,
                        Instant instant, OffsetDateTime offsetDateTime, Date utilDate) {
        this.name = name;
        this.localDate = localDate;
        this.localDateTime = localDateTime;
        this.instant = instant;
        this.offsetDateTime = offsetDateTime;
        this.utilDate = utilDate;
    }
}