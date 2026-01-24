package com.javarush.entity.polymorphism;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@DiscriminatorValue("EMAIL")
@Getter
@Setter
public class EmailNotification extends Notification {
    @Column(name = "sender_email")
    private String senderEmail;

    @Column(name = "cc_recipients")
    private String ccRecipients;

    @Column(name = "has_attachments")
    private Boolean hasAttachments = false;

    @Column(name = "email_template")
    private String emailTemplate;
}