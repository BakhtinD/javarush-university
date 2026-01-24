package com.javarush.entity.tableperclass;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(name = "movies")
@Getter
@Setter
public class Movie extends MediaItem {
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "director")
    private String director;

    @Column(name = "main_actors")
    private String mainActors;

    @Column(name = "budget")
    private Long budget;

    @Column(name = "box_office")
    private Long boxOffice;

    @Column(name = "has_oscars")
    private Boolean hasOscars = false;

    @Column(name = "imdb_id")
    private String imdbId;
}