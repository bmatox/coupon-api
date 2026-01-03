package com.coupon.api.controller;

import com.coupon.api.domain.entity.Coupon;
import com.coupon.api.domain.enums.CouponStatus;
import com.coupon.api.repository.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CouponControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CouponRepository repository;

    @Test
    @DisplayName("Deve criar um cupom com sucesso (End-to-End)")
    void shouldCreateCouponSuccessfully() throws Exception {
        String jsonRequest = """
                {
                    "code": "PROMO@1",
                    "description": "Cupom de Teste Integração",
                    "discountValue": 10.5,
                    "expirationDate": "2030-01-01T12:00:00.000Z",
                    "published": true
                }
                """;

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.code").value("PROMO1"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        Coupon savedCoupon = repository.findAll().stream()
                .filter(c -> c.getCode().equals("PROMO1"))
                .findFirst()
                .orElseThrow();

        assertNotNull(savedCoupon.getId());
    }

    @Test
    @DisplayName("Deve realizar soft delete via API")
    void shouldDeleteCouponSuccessfully() throws Exception {
        Coupon coupon = new Coupon();
        coupon.setCode("DEL123");
        coupon.setDescription("Cupom para deletar");
        coupon.setDiscountValue(new BigDecimal("5.0"));
        coupon.setStatus(CouponStatus.ACTIVE);
        coupon.setExpirationDate(LocalDateTime.now().plusDays(1));
        coupon.setPublished(true);
        coupon.setRedeemed(false);
        coupon = repository.save(coupon);

        mockMvc.perform(delete("/coupon/" + coupon.getId()))
                .andExpect(status().isNoContent());

        Coupon deletedCoupon = repository.findById(coupon.getId()).orElseThrow();
        assertEquals(CouponStatus.DELETED, deletedCoupon.getStatus());
        assertNotNull(deletedCoupon.getDeletedAt());
    }

    @Test
    @DisplayName("Deve buscar um cupom pelo ID")
    void shouldGetCouponById() throws Exception {
        Coupon coupon = new Coupon();
        coupon.setCode("GET123");
        coupon.setDescription("Cupom para busca");
        coupon.setDiscountValue(new BigDecimal("10.0"));
        coupon.setStatus(CouponStatus.ACTIVE);
        coupon.setExpirationDate(LocalDateTime.now().plusDays(1));
        coupon.setPublished(true);
        coupon.setRedeemed(false);
        coupon = repository.save(coupon);

        mockMvc.perform(get("/coupon/" + coupon.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("GET123"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando criar cupom inválido")
    void shouldReturn400WhenInvalidData() throws Exception {
        String jsonInvalid = """
                {
                    "code": "", 
                    "description": "Sem código",
                    "discountValue": -5,
                    "expirationDate": "2020-01-01T12:00:00.000Z"
                }
                """;

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Erro de validação nos dados enviados"))
                .andExpect(jsonPath("$.errors").isArray());
    }
}