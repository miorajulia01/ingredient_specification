package org.example.essaie.service;

import lombok.RequiredArgsConstructor;
import org.example.essaie.exception.NotFoundException;
import org.example.essaie.modele.Ingredient;
import org.example.essaie.modele.StockMovement;
import org.example.essaie.modele.StockValue;
import org.example.essaie.modele.UnitType;
import org.example.essaie.repository.IngredientRepository;
import org.example.essaie.repository.StockMovementRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final StockMovementRepository stockMovementRepository;

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.getIngredients();
    }

    public Ingredient findIngredientById(Integer id) {
        Optional<Ingredient> optionalIngredient = ingredientRepository.findById(id);
        if (optionalIngredient.isPresent()) {
            return optionalIngredient.get();
        }
        throw new NotFoundException("Ingredient.id = " + id + " not found");
    }

    public StockValue getStockValueAt(Integer idIngredient, Instant time, UnitType unit){
        Ingredient ing = findIngredientById(idIngredient);
        if (ing == null) {
            throw new NotFoundException("Ingredient.id = " + idIngredient + " not found");
        }
        List<StockMovement> move = stockMovementRepository.findStockById(idIngredient);
        ing.setStockMovementList(move);
        return ing.getStockValueAt(time, unit);
    }

}
