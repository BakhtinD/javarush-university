package com.javarush.entity.primarykey;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "online_orders")
@PrimaryKeyJoinColumn(name = "online_order_id", referencedColumnName = "order_id")
@Getter
@Setter
public class OnlineOrder extends BaseOrder {
    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Column(name = "shipping_address", nullable = false)
    private String shippingAddress;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "estimated_delivery_date")
    private LocalDateTime estimatedDeliveryDate;

    @Column(name = "promo_code_applied")
    private Boolean promoCodeApplied = false;

    // Дополнительный геттер для демонстрации
    public Long getOnlineOrderId() {
        return this.getOrderId(); // Возвращает то же значение
    }
}