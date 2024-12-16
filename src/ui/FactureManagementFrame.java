package ui;

import items.Customer;
import items.Facture;
import items.Order;
import items.Product;
import services.FactureService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FactureManagementFrame extends JFrame {
    private FactureService factureService;
    private DefaultTableModel tableModel;

    public FactureManagementFrame() {
        factureService = new FactureService();

        setTitle("Facture Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 表头
        String[] columnNames = {"Facture id", "Order ID", "Customer ID", "Total amount", "Date", "Detailed information"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable factureTable = new JTable(tableModel);

        // Setting up the rendering and editor for the details button
        factureTable.getColumn("Detailed information").setCellRenderer(new ButtonRenderer());
        factureTable.getColumn("Detailed information").setCellEditor(new ButtonEditor(new JCheckBox()));



        JScrollPane scrollPane = new JScrollPane(factureTable);
        add(scrollPane, BorderLayout.CENTER);


        loadFactures();
    }

    // Loading invoice data
    private void loadFactures() {
        tableModel.setRowCount(0); // 清空表格
        List<Facture> factures = factureService.getAllFactures();

        for (Facture facture : factures) {
            tableModel.addRow(new Object[]{
                    facture.getId(),
                    facture.getOrderId(),
                    facture.getCustomerId(),
                    String.format("%.2f", facture.getTotalAmount()),
                    facture.getIssueDate(),
                    "view"
            });
        }
    }

    // Show Details Window
    private void showFactureDetails(int factureId) {
        Facture facture = factureService.getFactureById(factureId);
        if (facture == null) {
            JOptionPane.showMessageDialog(this, "Invoice not found!");
            return;
        }

        Customer customer = factureService.getCustomerDetails(facture.getCustomerId());
        if (customer == null) {
            JOptionPane.showMessageDialog(this, "Customer information not found!");
            return;
        }

        Map<Product, Integer> orderDetails = factureService.getOrderDetails(facture.getOrderId());
        if (orderDetails == null || orderDetails.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Order details not found!");
            return;
        }


        double totalAmount = 0.0;
        for (Map.Entry<Product, Integer> entry : orderDetails.entrySet()) {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();
            totalAmount += product.getPrice() * quantity;
        }

        // Build invoice details
        StringBuilder details = new StringBuilder();
        details.append("Customer: ").append(customer.getName()).append("\n")
                .append("Date: ").append(facture.getIssueDate()).append("\n")
                .append("Order Details:\n");

        orderDetails.forEach((product, quantity) -> {
            details.append("- ").append(product.getName())
                    .append(" x").append(quantity)
                    .append(" @ ").append(String.format("%.2f", product.getPrice()))
                    .append("\n");
        });

        details.append("total amount: ").append(String.format("%.2f", totalAmount)).append("\n");

        // 显示发票详情
        JOptionPane.showMessageDialog(this, details.toString(), "Facture details", JOptionPane.INFORMATION_MESSAGE);
    }


    // 自定义按钮渲染器
    private class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Detailed information" : value.toString());
            return this;
        }
    }

    // 自定义按钮编辑器
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int selectedRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Detailed information");
            button.setOpaque(true);
            button.addActionListener((ActionEvent e) -> {
                int factureId = (int) tableModel.getValueAt(selectedRow, 0);
                showFactureDetails(factureId);
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
            return "Detailed information";
        }
    }


    public static void main(String[] args) {

        // Set the global language to English
        Locale.setDefault(Locale.ENGLISH);

        SwingUtilities.invokeLater(() -> {
            FactureManagementFrame frame = new FactureManagementFrame();
            frame.setVisible(true);
        });
    }
}
