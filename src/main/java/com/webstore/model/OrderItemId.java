package com.webstore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode
public class OrderItemId implements Serializable {

    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "product_id")
    private Integer productId;

    public OrderItemId(Integer orderId, Integer productId) {
        this.orderId = orderId;
        this.productId = productId;
    }
}
