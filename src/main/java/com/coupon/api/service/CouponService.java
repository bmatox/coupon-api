package com.coupon.api.service;

import com.coupon.api.domain.entity.Coupon;
import com.coupon.api.domain.enums.CouponStatus;
import com.coupon.api.domain.exception.BusinessException;
import com.coupon.api.dto.CouponRequestDTO;
import com.coupon.api.dto.CouponResponseDTO;
import com.coupon.api.mapper.CouponMapper;
import com.coupon.api.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository repository;
    private final CouponMapper mapper;

    @Transactional
    public CouponResponseDTO create(CouponRequestDTO request) {

        String codigoNormalizado = normalizarCodigo(request.code());
        validarTamanhoCodigo(codigoNormalizado);

        Coupon coupon = mapper.toEntity(request);

        coupon.setCode(codigoNormalizado);
        coupon.setStatus(CouponStatus.ACTIVE);

        Coupon savedCoupon = repository.save(coupon);
        return mapper.toResponse(savedCoupon);
    }

    public CouponResponseDTO findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new BusinessException("Cupom não encontrado."));
    }

    @Transactional
    public void delete(UUID id) {
        Coupon coupon = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Cupom não encontrado com o ID fornecido."));

        // Implementa o soft delete definido na especificação do negócio
        if (CouponStatus.DELETED.equals(coupon.getStatus())) {
            throw new BusinessException("Não deve ser possível deletar um cupom já deletado.");
        }

        coupon.setStatus(CouponStatus.DELETED);
        coupon.setDeletedAt(LocalDateTime.now());
        repository.save(coupon);
    }

    private String normalizarCodigo(String code) {
        if (code == null) return "";
        return code.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
    }

    private void validarTamanhoCodigo(String code) {
        if (code.length() != 6) {
            throw new BusinessException("O código do cupom deve ter exatamente 6 caracteres alfanuméricos após a normalização.");
        }
    }
}