package com.javarush.entity;

import lombok.*;

import javax.persistence.*;
import java.time.*;

@Entity
@Table(name = "modern_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ModernEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    // 1. DATE -> LocalDate
    @Column(name = "event_date")
    private LocalDate eventDate;

    // 2. TIME -> LocalTime
    @Column(name = "event_time")
    private LocalTime eventTime;

    // 3. TIMESTAMP -> LocalDateTime (самый частый)
    @Column(name = "event_datetime")
    private LocalDateTime eventDateTime;

    // 4. TIMESTAMP -> Instant (момент на временной шкале UTC)
    @Column(name = "event_instant")
    private Instant eventInstant;

    // 5. TIMESTAMP WITH TIME ZONE -> OffsetDateTime (смещение)
    @Column(name = "event_offset")
    private OffsetDateTime eventOffset;

    // 6. TIMESTAMP WITH TIME ZONE -> ZonedDateTime (полный часовой пояс)
    @Column(name = "event_zoned")
    private ZonedDateTime eventZoned;

    public ModernEvent(String name, LocalDate eventDate, LocalTime eventTime,
                       LocalDateTime eventDateTime, Instant eventInstant,
                       OffsetDateTime eventOffset, ZonedDateTime eventZoned) {
        this.name = name;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventDateTime = eventDateTime;
        this.eventInstant = eventInstant;
        this.eventOffset = eventOffset;
        this.eventZoned = eventZoned;
    }
}