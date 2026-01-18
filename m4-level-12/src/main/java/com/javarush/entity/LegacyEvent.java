package com.javarush.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "legacy_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LegacyEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    // 1. Без @Temporal -> по умолчанию TIMESTAMP (дата + время)
    @Column(name = "event_timestamp")
    private Date eventTimestamp;

    // 2. Только дата
    @Temporal(TemporalType.DATE)
    @Column(name = "event_date")
    private Date eventDate;

    // 3. Только время
    @Temporal(TemporalType.TIME)
    @Column(name = "event_time")
    private Date eventTime;

    // 4. Явно указанный TIMESTAMP (как по умолчанию)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "event_timestamp_explicit")
    private Date eventTimestampExplicit;

    public LegacyEvent(String name, Date eventTimestamp, Date eventDate,
                       Date eventTime, Date eventTimestampExplicit) {
        this.name = name;
        this.eventTimestamp = eventTimestamp;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventTimestampExplicit = eventTimestampExplicit;
    }
}