package com.javarush.entity.polymorphism;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@DiscriminatorValue("PUSH")
@Getter
@Setter
public class PushNotification extends Notification {
    @Column(name = "device_token")
    private String deviceToken;

    @Column(name = "platform")
    private String platform; // IOS, ANDROID, WEB

    @Column(name = "badge_count")
    private Integer badgeCount;

    @Column(name = "sound_enabled")
    private Boolean soundEnabled = true;
}