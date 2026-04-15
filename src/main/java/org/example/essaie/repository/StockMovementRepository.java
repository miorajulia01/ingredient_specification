package org.example.essaie.repository;

import lombok.RequiredArgsConstructor;
import org.example.essaie.modele.MovementTypeEnum;
import org.example.essaie.modele.StockMovement;
import org.example.essaie.modele.StockValue;
import org.example.essaie.modele.UnitType;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StockMovementRepository {
    private final DataSource dataSource;

    public List<StockMovement> findStockById(int id) {
        String sql = "SELECT id, quantity, unit, type, creation_datetime FROM stock_movement WHERE id = ?";
        List<StockMovement> result = new ArrayList<>();
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);

            try(ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    StockMovement sk = new StockMovement();
                    sk.setId(rs.getInt("id"));
                    sk.setType(MovementTypeEnum.valueOf(rs.getString("type")));
                    sk.setCreationDatetime(rs.getTimestamp("creation_datetime").toInstant());


                    StockValue sv = new StockValue();
                    sv.setQuantity(rs.getDouble("quantity"));
                    sv.setUnit(UnitType.valueOf(rs.getString("unit")));
                    sk.setValue(sv);
                    result.add(sk);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

}
