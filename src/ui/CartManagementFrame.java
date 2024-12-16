package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Locale;

import services.CartService;
import items.Cart;
import items.Customer;
import services.OrderService;

public class CartManagementFrame extends JFrame {
    private CartService cartService;
    private OrderService orderService;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;

    public CartManagementFrame() {
        this.cartService = new CartService();
        this.orderService = new OrderService();

        setTitle("Shopping Cart Management");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Tabular display of shopping cart contents
        String[] columnNames = {"Product ID", "Product Name", "Price", "Quantitiy", "total price", "operation"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable cartTable = new JTable(tableModel);

        // Custom button rendering and editing
        cartTable.getColumn("operation").setCellRenderer(new ButtonRenderer());
        cartTable.getColumn("operation").setCellEditor(new ButtonEditor(new JCheckBox(), cartTable));

        // Renderer and editor for custom quantity columns
        cartTable.getColumn("Quantitiy").setCellRenderer(new QuantityRenderer());
        cartTable.getColumn("Quantitiy").setCellEditor(new QuantityEditor(cartTable));

        JScrollPane scrollPane = new JScrollPane(cartTable);
        add(scrollPane, BorderLayout.CENTER);

        // Total amount display and complete order button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        totalLabel = new JLabel("total amount: 0.00");
        JButton checkoutButton = new JButton("pay");
        checkoutButton.addActionListener(e -> completeOrder());

        bottomPanel.add(totalLabel, BorderLayout.WEST);
        bottomPanel.add(checkoutButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        loadCartItems();
    }


    private void loadCartItems() {
        tableModel.setRowCount(0);
        double totalAmount = 0.0;

        List<Cart> cartItems = cartService.getAllCartItems();
        for (Cart cartItem : cartItems) {
            double totalPrice = cartItem.getPrice() * cartItem.getQuantity();

            totalAmount += totalPrice;
            tableModel.addRow(new Object[]{
                    cartItem.getProductId(),
                    cartItem.getProductName(),
                    String.format("%.2f", cartItem.getPrice()),
                    cartItem.getQuantity(),
                    String.format("%.2f", totalPrice),
                    "remove"
            });
        }

        totalLabel.setText("total amount: " + String.format("%.2f", totalAmount));
    }

    // Remove items from your shopping cart
    private void removeItemFromCart(int row, JTable cartTable) {
        // Stopping cell editing in a table
        if (cartTable.isEditing()) {
            cartTable.getCellEditor().stopCellEditing();
        }

        int productId = (int) tableModel.getValueAt(row, 0);
        boolean success = cartService.removeFromCartById(productId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Merchandise has been removed!");
        } else {
            JOptionPane.showMessageDialog(this, "Removal failed!");
        }
        loadCartItems();
    }

    // Complete your order and empty your shopping cart
    private void completeOrder() {

        Integer customerId = selectCustomer();
        if (customerId == null) {
            JOptionPane.showMessageDialog(this, "The order has not been completed and the customer has not been selected!");
            return;
        }


        boolean success = orderService.createOrder(customerId);
        if (success) {
            JOptionPane.showMessageDialog(this, "The order has been successfully created!");
            cartService.clearCart();
            loadCartItems();
        } else {
            JOptionPane.showMessageDialog(this, "Order creation failed, please try again!");
        }
    }

    // select a customer to finish the buy
    private Integer selectCustomer() {
        List<Customer> customers = orderService.getAllCustomers();
        String[] customerNames = customers.stream()
                .map(Customer::getName)
                .toArray(String[]::new);

        String selectedName = (String) JOptionPane.showInputDialog(
                this,
                "Please select a customer:",
                "Customer Choice",
                JOptionPane.PLAIN_MESSAGE,
                null,
                customerNames,
                null
        );

        if (selectedName == null) {
            return null;
        }


        return customers.stream()
                .filter(c -> c.getName().equals(selectedName))
                .findFirst()
                .map(Customer::getId)
                .orElse(null);
    }

    // Custom Button Renderer
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText("remove");
            return this;
        }
    }

    // Custom Button Editor
    private class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JButton button;
        private int selectedRow;

        public ButtonEditor(JCheckBox checkBox, JTable cartTable) {
            button = new JButton("remove");
            button.setOpaque(true);
            button.addActionListener((ActionEvent e) -> {
                removeItemFromCart(selectedRow, cartTable);
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            selectedRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "remove";
        }
    }

    // 自定义数量列的渲染器
    private class QuantityRenderer extends JPanel implements TableCellRenderer {
        public QuantityRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            add(new JLabel(value.toString()));
            return this;
        }
    }


    private class QuantityEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JTextField quantityField;
        private JButton incrementButton;
        private JButton decrementButton;
        private int selectedRow;

        public QuantityEditor(JTable cartTable) {
            panel = new JPanel(new BorderLayout());
            quantityField = new JTextField();
            incrementButton = new JButton("+");
            decrementButton = new JButton("-");

            incrementButton.addActionListener(e -> {
                try {
                    int quantity = Integer.parseInt(quantityField.getText());
                    quantity++;
                    quantityField.setText(String.valueOf(quantity));

                    updateQuantity(selectedRow, quantity, cartTable);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(CartManagementFrame.this, "Invalid quantity!");
                }
            });

            decrementButton.addActionListener(e -> {
                try {
                    int quantity = Integer.parseInt(quantityField.getText());
                    if (quantity > 1) {
                        quantity--;
                        quantityField.setText(String.valueOf(quantity));

                        updateQuantity(selectedRow, quantity, cartTable); // 更新数量和总价
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(CartManagementFrame.this, "Invalid quantity!");
                }
            });

            panel.add(decrementButton, BorderLayout.WEST);
            panel.add(quantityField, BorderLayout.CENTER);
            panel.add(incrementButton, BorderLayout.EAST);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            selectedRow = row;
            quantityField.setText(value.toString());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return quantityField.getText();
        }

        private void updateQuantity(int row, int quantity, JTable table) {
            int productId = (int) table.getValueAt(row, 0);
            boolean success = cartService.updateQuantity(productId, quantity); // 更新数据库中的数量
            if (success) {
                loadCartItems();
            } else {
                JOptionPane.showMessageDialog(CartManagementFrame.this, "Quantity update failed!");
            }
        }
    }




    public static void main(String[] args) {
        // Set the global language to English
        Locale.setDefault(Locale.ENGLISH);

        SwingUtilities.invokeLater(() -> new CartManagementFrame().setVisible(true));
    }
}





