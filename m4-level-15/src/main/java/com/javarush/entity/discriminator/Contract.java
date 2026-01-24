package com.javarush.entity.discriminator;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@DiscriminatorValue("CONTRACT")
// Для числового: @DiscriminatorValue("2")
// Для символьного: @DiscriminatorValue("C")
@Getter
@Setter
public class Contract extends Document {
    @Column(name = "contract_party_a")
    private String partyA;

    @Column(name = "contract_party_b")
    private String partyB;

    @Column(name = "valid_from")
    private java.time.LocalDate validFrom;

    @Column(name = "valid_to")
    private java.time.LocalDate validTo;

    @Column(name = "signature_date")
    private java.time.LocalDate signatureDate;

    @Column(name = "is_active")
    private Boolean isActive = true;
}