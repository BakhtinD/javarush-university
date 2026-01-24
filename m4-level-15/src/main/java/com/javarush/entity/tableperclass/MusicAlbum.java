package com.javarush.entity.tableperclass;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "music_albums")
@Getter
@Setter
public class MusicAlbum extends MediaItem {
    @Column(name = "artist")
    private String artist;

    @Column(name = "record_label")
    private String recordLabel;

    @Column(name = "track_count")
    private Integer trackCount;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "album_type")
    private String albumType; // STUDIO, LIVE, COMPILATION

    @Column(name = "format")
    private String format; // CD, VINYL, DIGITAL

    @Column(name = "upc_code")
    private String upcCode;
}