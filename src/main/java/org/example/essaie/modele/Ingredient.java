package org.example.essaie.modele;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class Ingredient {
    private Integer id;
    private String name;
    private CategoryEnum category;
    private Double price;
    private List<StockMovement> stockMovementList = new ArrayList<>();

    public Ingredient(){}

    public StockValue getStockValueAt(Instant t, UnitType unit) {
        double total = 0.0;
        for (StockMovement m : stockMovementList) {
            if (m.getCreationDatetime() != null && !m.getCreationDatetime().isAfter(t)) {
                if (m.getType() == MovementTypeEnum.IN) {
                    total += m.getValue().getQuantity();
                } else if (m.getType() == MovementTypeEnum.OUT) {
                    total -= m.getValue().getQuantity();
                }
            }
        }
        return new StockValue(total, UnitType.KG);
    }
}
