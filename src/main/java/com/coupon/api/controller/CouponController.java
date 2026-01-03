package com.coupon.api.controller;

import com.coupon.api.dto.CouponRequestDTO;
import com.coupon.api.dto.CouponResponseDTO;
import com.coupon.api.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
@Tag(name = "Coupon", description = "Gerenciamento de cupons de desconto")
public class CouponController {

    private final CouponService service;

    @PostMapping
    @Operation(summary = "Criar um novo cupom", description = "Cria um cupom aplicando normalização de código e regras de validação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cupom criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (ex: data no passado, valor negativo)")
    })
    public ResponseEntity<CouponResponseDTO> create(@RequestBody @Valid CouponRequestDTO request) {
        CouponResponseDTO response = service.create(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cupom por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cupom encontrado"),
            @ApiResponse(responseCode = "404", description = "Cupom não encontrado")
    })
    public ResponseEntity<CouponResponseDTO> getById(@PathVariable UUID id) {
        CouponResponseDTO response = service.findById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar cupom (Soft Delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cupom deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cupom não encontrado"),
            @ApiResponse(responseCode = "400", description = "Tentativa de deletar cupom já removido")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}