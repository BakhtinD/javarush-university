package com.javarush.entity;

import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "discount", precision = 5, scale = 2)
    private BigDecimal discount;

    // Many-to-One с конкретными cascade типами
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "order_id")
    private Order order;

    public OrderItem(String productName, Integer quantity, BigDecimal price, BigDecimal discount) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
    }

    // Метод для расчёта общей стоимости
    public BigDecimal getTotalPrice() {
        BigDecimal discountedPrice = price.multiply(
                BigDecimal.ONE.subtract(discount.divide(BigDecimal.valueOf(100)))
        );
        return discountedPrice.multiply(BigDecimal.valueOf(quantity));
    }
}