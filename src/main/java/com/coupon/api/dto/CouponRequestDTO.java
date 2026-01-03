package com.coupon.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponRequestDTO(
        @NotBlank(message = "O código é obrigatório")
        String code,

        @NotBlank(message = "A descrição é obrigatória")
        String description,

        @NotNull(message = "O valor de desconto é obrigatório")
        @DecimalMin(value = "0.5", message = "O valor de desconto mínimo é 0,5")
        BigDecimal discountValue,

        @NotNull(message = "A data de expiração é obrigatória")
        @Future(message = "A data de expiração deve ser no futuro")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        LocalDateTime expirationDate,

        Boolean published
) {}