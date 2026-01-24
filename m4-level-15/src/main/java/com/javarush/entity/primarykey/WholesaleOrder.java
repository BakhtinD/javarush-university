package com.javarush.entity.primarykey;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "wholesale_orders")
@PrimaryKeyJoinColumn(
        name = "wholesale_ref_id", // Имя колонки в этой таблице
        referencedColumnName = "order_id", // Имя колонки в родительской таблице
        columnDefinition = "BIGINT NOT NULL"
)
@Getter
@Setter
public class WholesaleOrder extends BaseOrder {
    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "tax_id", nullable = false)
    private String taxId;

    @Column(name = "delivery_terms")
    private String deliveryTerms; // FOB, CIF, EXW

    @Column(name = "payment_terms_days")
    private Integer paymentTermsDays = 30;

    @Column(name = "purchase_order_number")
    private String purchaseOrderNumber;

    @Column(name = "is_recurring")
    private Boolean isRecurring = false;

    @Column(name = "contract_valid_until")
    private LocalDate contractValidUntil;
}