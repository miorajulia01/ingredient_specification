package org.example.essaie.modele;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class DishIngredient {
    @JsonIgnore
    private Integer id;

    @JsonIgnore
    private Dish dish;
    private Ingredient ingredient;
    private Double quantityRequired;

    @JsonIgnore
    private UnitType unit;

    public DishIngredient() {}
}
