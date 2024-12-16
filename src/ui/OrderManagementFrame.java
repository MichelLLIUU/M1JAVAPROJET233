package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import services.OrderService;
import items.Order;

public class OrderManagementFrame extends JFrame {
    private OrderService orderService;
    private DefaultTableModel tableModel;

    public OrderManagementFrame() {
        orderService = new OrderService();

        setTitle("Order Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        String[] columnNames = {"Order ID", "Client Name", "total amount", "state of affairs", "times", "operation",  "Remove"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable orderTable = new JTable(tableModel);

        TableColumnModel columnModel = orderTable.getColumnModel();
        columnModel.getColumn(5).setCellRenderer(new ButtonRenderer());
        columnModel.getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));
        columnModel.getColumn(6).setCellRenderer(new ButtonRenderer());
        columnModel.getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(orderTable);
        add(scrollPane, BorderLayout.CENTER);

        loadOrders(); // 加载订单数据
    }

    private void loadOrders() {
        tableModel.setRowCount(0); // 清空表格数据
        List<Order> orders = orderService.getAllOrders();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Order order : orders) {
            tableModel.addRow(new Object[]{
                    order.getId(),
                    order.getCustomer().getName(),
                    String.format("%.2f", order.getTotalAmount()),
                    order.getStatus(),
                    dateFormat.format(order.getDate()),
                    "Generate Invoice",
                    "Remove"
            });
        }
    }


    // Render button
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }


    // 按钮事件
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int selectedOrderId;
        private int column;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.column = column;
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            selectedOrderId = (int) table.getValueAt(row, 0); // 获取订单 ID
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                if (column == 5) { // 生成发票按钮
                    handleGenerateInvoice(selectedOrderId);
                } else if (column == 6) { // 删除按钮
                    handleRemoveOrder(selectedOrderId);
                }
            }
            isPushed = false;
            return label;
        }

        private void handleGenerateInvoice(int orderId) {
            Order order = orderService.getAllOrders().stream()
                    .filter(o -> o.getId() == orderId)
                    .findFirst()
                    .orElse(null);
            if (order == null || !"on going".equals(order.getStatus())) {
                JOptionPane.showMessageDialog(button, "Cannot generate invoice for a non-on going order!");
                return;
            }
            boolean success = orderService.generateFacture(orderId);
            if (success) {
                JOptionPane.showMessageDialog(button, "Invoice generated successfully!");
                loadOrders();
            } else {
                JOptionPane.showMessageDialog(button, "Failed to generate invoice.");
            }
        }

        private void handleRemoveOrder(int orderId) {
            Order order = orderService.getAllOrders().stream()
                    .filter(o -> o.getId() == orderId)
                    .findFirst()
                    .orElse(null);
            if (order == null) {
                JOptionPane.showMessageDialog(button, "Order not found!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(button, "Are you sure you want to cancel this order?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = orderService.cancelOrder(orderId);
                if (success) {
                    JOptionPane.showMessageDialog(button, "Order canceled successfully!");
                    loadOrders();
                } else {
                    JOptionPane.showMessageDialog(button, "Failed to cancel the order.");
                }
            }
        }
    }



    public static void main(String[] args) {

        // Set the global language to English
        Locale.setDefault(Locale.ENGLISH);

        SwingUtilities.invokeLater(() -> new OrderManagementFrame().setVisible(true));
    }
}
