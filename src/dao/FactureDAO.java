package dao;

import dataBase.DataBaseConnection;
import items.Facture;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FactureDAO {

    private Connection connection;

    public FactureDAO() {
        this.connection = DataBaseConnection.getConnection();
    }

    public List<Facture> getAllFactures() {
        List<Facture> factures = new ArrayList<>();
        String query = "SELECT id, order_id, customer_id, total_amount, issue_date FROM factures";

        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Facture facture = new Facture(
                        resultSet.getInt("id"),
                        resultSet.getInt("order_id"),
                        resultSet.getInt("customer_id"),
                        resultSet.getDouble("total_amount"),
                        resultSet.getTimestamp("issue_date")
                );
                factures.add(facture);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return factures;
    }

    // Get invoice based on ID
    public Facture getFactureById(int id) {
        String query = "SELECT * FROM factures WHERE id = ?";
        Facture facture = null;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    facture = new Facture(
                            rs.getInt("id"),
                            rs.getInt("order_id"),
                            rs.getInt("customer_id"),
                            rs.getDouble("total_amount"),
                            rs.getDate("issue_date")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return facture;
    }

    public boolean generateFacture(int orderId) {
        String generateFactureQuery = """
        INSERT INTO factures (order_id, customer_id, total_amount, issue_date)
        SELECT id, customer_id, total_amount, CURRENT_TIMESTAMP
        FROM orders
        WHERE id = ?;
    """;
        String updateOrderStatusQuery = "UPDATE orders SET status = 'finished' WHERE id = ?";

        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement factureStmt = connection.prepareStatement(generateFactureQuery);
             PreparedStatement statusStmt = connection.prepareStatement(updateOrderStatusQuery)) {

            // Generate facture
            factureStmt.setInt(1, orderId);
            int factureInserted = factureStmt.executeUpdate();

            if (factureInserted > 0) {
                // Update order status
                statusStmt.setInt(1, orderId);
                statusStmt.executeUpdate();
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error generating facture or updating order status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
