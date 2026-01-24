package com.javarush.entity.primarykey;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(name = "store_orders")
// Без @PrimaryKeyJoinColumn - будет использовано имя по умолчанию
@Getter
@Setter
public class StoreOrder extends BaseOrder {
    @Column(name = "store_location", nullable = false)
    private String storeLocation;

    @Column(name = "cashier_id")
    private String cashierId;

    @Column(name = "receipt_number")
    private String receiptNumber;

    @Column(name = "payment_type")
    private String paymentType; // CASH, CARD

    @Column(name = "customer_feedback_rating")
    private Integer customerFeedbackRating; // 1-5
}