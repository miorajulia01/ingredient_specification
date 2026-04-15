package org.example.essaie.repository;

import lombok.RequiredArgsConstructor;
import org.example.essaie.modele.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DishRepository {

    private final DataSource dataSource;

    public List<Dish> findAll() {
        List<Dish> result = new ArrayList<>();

        String sqlDish = "SELECT id, name, dish_type, selling_price FROM dish ORDER BY id";
        String sqlIngredients = """
                SELECT di.quantity_required, 
                       i.id as ingredient_id, 
                       i.name as ingredient_name, 
                       i.price as ingredient_price, 
                       i.category
                FROM dish_ingredient di
                JOIN ingredient i ON di.id_ingredient = i.id
                WHERE di.id_dish = ?
                ORDER BY i.name
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement psDish = conn.prepareStatement(sqlDish);
             ResultSet rsDish = psDish.executeQuery();
             PreparedStatement psIng = conn.prepareStatement(sqlIngredients)) {

            while (rsDish.next()) {
                Dish dish = new Dish();
                int dishId = rsDish.getInt("id");
                dish.setId(dishId);
                dish.setName(rsDish.getString("name"));
                dish.setDishType(DishTypeEnum.valueOf(rsDish.getString("dish_type")));
                dish.setSellingPrice(rsDish.getDouble("selling_price"));
                dish.setDishIngredients(new ArrayList<>());

                psIng.setInt(1, dishId);
                try (ResultSet rsIng = psIng.executeQuery()) {
                    while (rsIng.next()) {
                        Ingredient ing = new Ingredient();
                        ing.setId(rsIng.getInt("ingredient_id"));
                        ing.setName(rsIng.getString("ingredient_name"));
                        ing.setPrice(rsIng.getDouble("ingredient_price"));
                        ing.setCategory(CategoryEnum.valueOf(rsIng.getString("category")));

                        DishIngredient di = new DishIngredient();
                        di.setIngredient(ing);
                        di.setQuantityRequired(rsIng.getDouble("quantity_required"));
                        dish.getDishIngredients().add(di);
                    }
                }
                result.add(dish);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erreur lors de la récupération des plats", ex);
        }
        return result;
    }

    public Dish findDishById(Integer id) {
        String sql = "SELECT id, name, dish_type, selling_price FROM dish WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Dish dish = new Dish();
                    dish.setId(rs.getInt("id"));
                    dish.setName(rs.getString("name"));
                    dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                    dish.setSellingPrice(rs.getDouble("selling_price"));
                    dish.setDishIngredients(new ArrayList<>());
                    return dish;
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }

    @Transactional
    public void updateDishIngredients(Integer dishId, List<DishIngredient> ingredients) {
        String deleteSql = "DELETE FROM dish_ingredient WHERE id_dish = ?";
        String insertSql = "INSERT INTO dish_ingredient (id_dish, id_ingredient, quantity_required, unit) VALUES (?, ?, ?, ?::unit_type)";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try {
                try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
                    psDelete.setInt(1, dishId);
                    psDelete.executeUpdate();
                }

                if (ingredients != null && !ingredients.isEmpty()) {
                    try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                        for (DishIngredient di : ingredients) {
                            if (di.getIngredient() != null) {
                                psInsert.setInt(1, dishId);
                                psInsert.setInt(2, di.getIngredient().getId());
                                psInsert.setDouble(3, di.getQuantityRequired());
                                String unitValue = (di.getUnit() != null) ? di.getUnit().name() : "PCS";
                                psInsert.setString(4, unitValue);
                                psInsert.executeUpdate();
                            }
                        }
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Erreur lors de la mise à jour des ingrédients", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}