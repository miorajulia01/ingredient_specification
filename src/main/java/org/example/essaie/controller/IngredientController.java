package org.example.essaie.controller;

import lombok.RequiredArgsConstructor;
import org.example.essaie.exception.BadRequestException;
import org.example.essaie.exception.NotFoundException;
import org.example.essaie.modele.Ingredient;
import org.example.essaie.modele.StockValue;
import org.example.essaie.modele.UnitType;
import org.example.essaie.service.IngredientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ingredients")
public class IngredientController {
    private final IngredientService ingredientService;

    @GetMapping()
    public ResponseEntity<?> getIngredients() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(ingredientService.getAllIngredients());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getIngredientById(@PathVariable(name = "id") Integer ingredientId) {
        try {
            Ingredient ing = ingredientService.findIngredientById(ingredientId);
            return ResponseEntity.status(HttpStatus.OK).body(ing);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<?> getStockMovementById(
            @PathVariable(name = "id") Integer idIngredient,
            @RequestParam(name = "at", required = false) Instant at,
            @RequestParam(name = "unit", required = false) UnitType unit) {
        if (at == null || unit == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Either mandatory query parameter `at` or `unit` is not provided.");
        }
        try {
            StockValue stockValue = ingredientService.getStockValueAt(idIngredient, at, unit);
            return ResponseEntity.status(HttpStatus.OK).body(stockValue);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ingredient.id=" + idIngredient + " is not found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}

