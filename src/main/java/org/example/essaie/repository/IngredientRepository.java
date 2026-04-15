package org.example.essaie.repository;

import lombok.RequiredArgsConstructor;
import org.example.essaie.modele.CategoryEnum;
import org.example.essaie.modele.Ingredient;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor

public class IngredientRepository {
    private final DataSource dataSource;

    public List<Ingredient> getIngredients() {
        String sql = "select id, name, price, category from ingredient order by id";
        List<Ingredient> result = new ArrayList<>();
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Ingredient ing = new Ingredient();
                ing.setId(rs.getInt("id"));
                ing.setName(rs.getString("name"));
                ing.setPrice(rs.getDouble("price"));
                ing.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                result.add(ing);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public Optional<Ingredient> findById(Integer id) {
        String sql = "select id, name, price, category from ingredient where id = ?";
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setInt(1, id);

          try(   ResultSet rs = ps.executeQuery()) {
              if (rs.next()) {
                  Ingredient ing = new Ingredient();
                  ing.setId(rs.getInt("id"));
                  ing.setName(rs.getString("name"));
                  ing.setPrice(rs.getDouble("price"));
                  ing.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                  return Optional.of(ing);
              }
          }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
}