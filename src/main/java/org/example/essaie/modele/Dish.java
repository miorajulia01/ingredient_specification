package org.example.essaie.modele;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class Dish {
    private Integer id;
    private String name;
    private DishTypeEnum dishType;
    private Double sellingPrice;
    private List<DishIngredient> dishIngredients;

    public Dish (){}

    @JsonIgnore
    public List<Ingredient> getIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        if (dishIngredients != null) {
            for (DishIngredient di : dishIngredients) {
                if (di != null && di.getIngredient() != null) {
                    ingredients.add(di.getIngredient());
                }
            }
        }
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        if (ingredients == null) {
            this.dishIngredients = new ArrayList<>();
            return;
        }

        List<DishIngredient> newDishIngredients = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            if (ingredient != null) {
                DishIngredient di = new DishIngredient();
                di.setIngredient(ingredient);
                di.setQuantityRequired(1.0);
                di.setUnit(UnitType.PC);
                newDishIngredients.add(di);
            }
        }
        this.dishIngredients = newDishIngredients;
    }


    public Double getDishCost() {
        double totalCost = 0.0;

        if (dishIngredients == null || dishIngredients.isEmpty()) {
            return totalCost;
        }

        for (DishIngredient dishIngredient : dishIngredients) {
            if (dishIngredient != null) {
                Ingredient ingredient = dishIngredient.getIngredient();
                Double ingredientPrice = ingredient != null ? ingredient.getPrice() : null;
                Double quantity = dishIngredient.getQuantityRequired();

                if (ingredientPrice != null && quantity != null) {
                    totalCost += ingredientPrice * quantity;
                }
            }
        }
        return totalCost;
    }

    public Double getGrossMargin() {
        if (sellingPrice == null) {
            throw new IllegalStateException("Impossible de calculer la marge : " +
                    "le prix de vente n'est pas encore fixé.");
        }
        return sellingPrice - getDishCost();
    }


    public void addIngredient(Ingredient ingredient, Double quantity, UnitType unit) {
        if (ingredient == null) return;

        DishIngredient di = new DishIngredient();
        di.setIngredient(ingredient);
        di.setQuantityRequired(quantity);
        di.setUnit(unit);

        if (dishIngredients == null) {
            dishIngredients = new ArrayList<>();
        }
        dishIngredients.add(di);
    }


    public DishIngredient findDishIngredient(Integer ingredientId) {
        if (dishIngredients == null || ingredientId == null) return null;

        return dishIngredients.stream()
                .filter(di -> di != null && di.getIngredient() != null)
                .filter(di -> ingredientId.equals(di.getIngredient().getId()))
                .findFirst()
                .orElse(null);
    }
}
