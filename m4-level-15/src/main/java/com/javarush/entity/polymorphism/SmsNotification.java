package com.javarush.entity.polymorphism;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@DiscriminatorValue("SMS")
@Getter
@Setter
public class SmsNotification extends Notification {
    @Column(name = "sender_number")
    private String senderNumber;

    @Column(name = "is_international")
    private Boolean isInternational = false;

    @Column(name = "character_count")
    private Integer characterCount;
}