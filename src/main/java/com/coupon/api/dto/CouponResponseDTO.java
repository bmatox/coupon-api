package com.coupon.api.dto;

import com.coupon.api.domain.enums.CouponStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CouponResponseDTO(
        UUID id,
        String code,
        String description,
        BigDecimal discountValue,
        LocalDateTime expirationDate,
        Boolean published,
        Boolean redeemed,
        CouponStatus status
) {}