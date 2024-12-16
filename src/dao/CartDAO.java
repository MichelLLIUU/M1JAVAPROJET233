package dao;

import dataBase.DataBaseConnection;
import items.Cart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {
    private Connection connection;

    public CartDAO() {
        connection = DataBaseConnection.getConnection(); // Obtaining a database connection
    }

    // Get all items in the shopping cart
    public List<Cart> getAllCartItems() {
        List<Cart> cartItems = new ArrayList<>();
        String query = "SELECT id, product_id, product_name, price, quantity FROM cart_items";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cart cartItem = new Cart(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                );
                cartItems.add(cartItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cartItems;
    }

    // Get the products in the shopping cart according to the product ID.
    public Cart getCartItemByProductId(int productId) {
        String query = "SELECT id, product_id, product_name, price, quantity FROM cart_items WHERE product_id = ?";
        Cart cartItem = null;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cartItem = new Cart(
                            rs.getInt("id"),
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getDouble("price"),
                            rs.getInt("quantity")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cartItem;
    }

    // Add new item to cart
    public void addCartItem(int productId, String productName, double price, int quantity) {
        String query = "INSERT INTO cart_items (product_id, product_name, price, quantity) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productId);
            stmt.setString(2, productName);
            stmt.setDouble(3, price);
            stmt.setInt(4, quantity);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update the number of items in the shopping cart
    public void updateCartItemQuantity(int productId, int quantity) {
        String query = "UPDATE cart_items SET quantity = ? WHERE product_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Deleting items in the shopping cart
    public void removeCartItem(int productId) {
        String query = "DELETE FROM cart_items WHERE product_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Empty Cart
    public void clearCart() {
        String query = "DELETE FROM cart_items";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateQuantity(int productId, int newQuantity) {
        String query = "UPDATE cart_items SET quantity = ? WHERE product_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, newQuantity);
            preparedStatement.setInt(2, productId);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}


