package org.example.essaie.modele;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StockValue {
    private double quantity;
    private UnitType unit;
}
