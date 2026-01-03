package com.coupon.api.domain.entity;

import com.coupon.api.domain.enums.CouponStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 6)
    private String code;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal discountValue;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @Column(nullable = false)
    private Boolean published;

    @Column(nullable = false)
    private Boolean redeemed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatus status;

    private LocalDateTime deletedAt;
}