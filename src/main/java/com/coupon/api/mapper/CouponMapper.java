package com.coupon.api.mapper;

import com.coupon.api.domain.entity.Coupon;
import com.coupon.api.dto.CouponRequestDTO;
import com.coupon.api.dto.CouponResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class CouponMapper {

    public Coupon toEntity(CouponRequestDTO dto) {
        Coupon coupon = new Coupon();
        coupon.setDescription(dto.description());
        coupon.setDiscountValue(dto.discountValue());
        coupon.setExpirationDate(dto.expirationDate());
        coupon.setPublished(dto.published() != null ? dto.published() : false);
        coupon.setRedeemed(false);
        return coupon;
    }

    public CouponResponseDTO toResponse(Coupon coupon) {
        return new CouponResponseDTO(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDescription(),
                coupon.getDiscountValue(),
                coupon.getExpirationDate(),
                coupon.getPublished(),
                coupon.getRedeemed(),
                coupon.getStatus()
        );
    }
}