package dao;

import dataBase.DataBaseConnection;
import items.Cart;
import items.Customer;
import items.Order;
import items.Product;

import java.sql.*;
import java.util.*;
import java.util.Date;


public class OrderDAO {
    private Connection connection;

    public OrderDAO() {
        connection = DataBaseConnection.getConnection();
    }


    public boolean createOrder(Customer customer, List<Cart> cartItems) {
        String insertOrder = "INSERT INTO orders (customer_id, total_amount) VALUES (?, ?)";
        String insertOrderItems = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

        try {
            connection.setAutoCommit(false);

            // Insert Order Information
            PreparedStatement orderStmt = connection.prepareStatement(insertOrder, PreparedStatement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, customer.getId());
            double totalAmount = cartItems.stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();
            orderStmt.setDouble(2, totalAmount);
            orderStmt.executeUpdate();

            // Get the generated order ID
            int orderId = 0;
            try (var rs = orderStmt.getGeneratedKeys()) {
                if (rs.next()) {
                    orderId = rs.getInt(1);
                }
            }

            // Insert order product information
            PreparedStatement itemStmt = connection.prepareStatement(insertOrderItems);
            for (Cart cartItem : cartItems) {
                itemStmt.setInt(1, orderId);
                itemStmt.setInt(2, cartItem.getProductId());
                itemStmt.setInt(3, cartItem.getQuantity());
                itemStmt.setDouble(4, cartItem.getPrice());
                itemStmt.addBatch();
            }
            itemStmt.executeBatch();

            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }



    // Get the product list of an order
    public Map<Product, Integer> getOrderItemsByOrderId(int orderId) {
        String query = "SELECT p.id, p.name, p.price, oi.quantity FROM order_items oi " +
                "JOIN products p ON oi.product_id = p.id WHERE oi.order_id = ?";
        Map<Product, Integer> items = new HashMap<>();

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDouble("price")
                    );
                    items.put(product, rs.getInt("quantity"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    // Get all orders
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String query = """
                SELECT o.id AS order_id, o.total_amount, o.status, o.order_date,
                                                               c.id AS customer_id, c.name AS customer_name, c.email, c.phone, c.address
                                                        FROM orders o
                                                        JOIN customers c ON o.customer_id = c.id
                """;

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Customer customer = new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("customer_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address")
                );


                Timestamp orderDate = rs.getTimestamp("order_date");
                Date date = (orderDate != null) ? new Date(orderDate.getTime()) : new Date(); // 设置默认时间
                Order order = new Order(
                        rs.getInt("order_id"),
                        customer,
                        rs.getDouble("total_amount"),
                        date,
                        rs.getString("status"),
                        rs.getString("address")
                );

                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    public boolean updateOrderStatus(int orderId, String newStatus) {
        String query = "UPDATE orders SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, orderId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean cancelOrder(int orderId) {
        String updateOrderStatusQuery = "UPDATE orders SET status = 'canceled' WHERE id = ?";
        String deleteFactureQuery = "DELETE FROM factures WHERE order_id = ?";

        try (PreparedStatement updateOrderStmt = connection.prepareStatement(updateOrderStatusQuery);
             PreparedStatement deleteFactureStmt = connection.prepareStatement(deleteFactureQuery)) {

            updateOrderStmt.setInt(1, orderId);
            updateOrderStmt.executeUpdate();

            deleteFactureStmt.setInt(1, orderId);
            deleteFactureStmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }




}

