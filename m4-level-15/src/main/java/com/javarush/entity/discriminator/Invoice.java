package com.javarush.entity.discriminator;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@DiscriminatorValue("INVOICE") // Строковое значение
// Для числового: @DiscriminatorValue("1")
// Для символьного: @DiscriminatorValue("I")
@Getter
@Setter
public class Invoice extends Document {
    @Column(name = "invoice_number", unique = true)
    private String invoiceNumber;

    @Column(name = "total_amount")
    private java.math.BigDecimal totalAmount;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "due_date")
    private java.time.LocalDate dueDate;

    @Column(name = "is_paid")
    private Boolean isPaid = false;
}