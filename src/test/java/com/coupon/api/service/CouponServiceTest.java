package com.coupon.api.service;

import com.coupon.api.domain.entity.Coupon;
import com.coupon.api.domain.enums.CouponStatus;
import com.coupon.api.domain.exception.BusinessException;
import com.coupon.api.dto.CouponRequestDTO;
import com.coupon.api.dto.CouponResponseDTO;
import com.coupon.api.mapper.CouponMapper;
import com.coupon.api.repository.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @InjectMocks
    private CouponService service;

    @Mock
    private CouponRepository repository;

    @Mock
    private CouponMapper mapper;

    @Test
    @DisplayName("Deve criar cupom normalizando código com caracteres especiais")
    void shouldCreateCouponNormalizeCode() {
        CouponRequestDTO request = new CouponRequestDTO(
                "A@B#C$123",
                "Desconto Teste",
                new BigDecimal("10.0"),
                LocalDateTime.now().plusDays(1),
                true
        );

        Coupon couponEntity = new Coupon();
        couponEntity.setDiscountValue(request.discountValue());

        Coupon savedCoupon = new Coupon();
        savedCoupon.setId(UUID.randomUUID());
        savedCoupon.setCode("ABC123");
        savedCoupon.setStatus(CouponStatus.ACTIVE);

        CouponResponseDTO expectedResponse = new CouponResponseDTO(
                savedCoupon.getId(), "ABC123", "Desc", BigDecimal.TEN, LocalDateTime.now(), true, false, CouponStatus.ACTIVE
        );

        when(mapper.toEntity(request)).thenReturn(couponEntity);
        when(repository.save(any(Coupon.class))).thenReturn(savedCoupon);
        when(mapper.toResponse(savedCoupon)).thenReturn(expectedResponse);

        CouponResponseDTO response = service.create(request);

        assertNotNull(response.id());
        assertEquals("ABC123", response.code());
        assertEquals("ABC123", couponEntity.getCode());
        verify(repository, times(1)).save(any(Coupon.class));
    }

    @Test
    @DisplayName("Deve lançar erro quando código normalizado não tiver 6 caracteres")
    void shouldThrowErrorWhenCodeSizeIsInvalid() {
        CouponRequestDTO request = new CouponRequestDTO(
                "A@B#",
                "Desc",
                BigDecimal.ONE,
                LocalDateTime.now().plusDays(1),
                false
        );

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            service.create(request);
        });

        assertEquals("O código do cupom deve ter exatamente 6 caracteres alfanuméricos após a normalização.", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve realizar soft delete com sucesso")
    void shouldSoftDeleteSuccessfully() {
        UUID id = UUID.randomUUID();
        Coupon coupon = new Coupon();
        coupon.setId(id);
        coupon.setStatus(CouponStatus.ACTIVE);

        when(repository.findById(id)).thenReturn(Optional.of(coupon));

        service.delete(id);

        assertEquals(CouponStatus.DELETED, coupon.getStatus());
        assertNotNull(coupon.getDeletedAt());
        verify(repository, times(1)).save(coupon);
    }

    @Test
    @DisplayName("Deve impedir delete de cupom já deletado")
    void shouldPreventDoubleDelete() {
        UUID id = UUID.randomUUID();
        Coupon coupon = new Coupon();
        coupon.setId(id);
        coupon.setStatus(CouponStatus.DELETED);

        when(repository.findById(id)).thenReturn(Optional.of(coupon));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            service.delete(id);
        });

        assertEquals("Não deve ser possível deletar um cupom já deletado.", exception.getMessage());
        verify(repository, never()).save(any());
    }
}