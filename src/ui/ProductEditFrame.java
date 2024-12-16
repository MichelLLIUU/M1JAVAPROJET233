package ui;

import services.ProductService;
import items.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductEditFrame extends JFrame {
    private ProductService productService;
    private DefaultTableModel tableModel;
    private JTable productTable;

    public ProductEditFrame(ProductService productService) {
        this.productService = productService;

        setTitle("Product Editor");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 商品表格
        String[] columnNames = {"ID", "name", "price", "URL"};
        tableModel = new DefaultTableModel(columnNames, 0);
        productTable = new JTable(tableModel);
        loadProducts();

        // Add, Update and Delete buttons
        JButton addButton = new JButton("add");
        JButton updateButton = new JButton("modify");
        JButton deleteButton = new JButton("remove");

        addButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        add(new JScrollPane(productTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadProducts() {
        tableModel.setRowCount(0);
        List<Product> products = productService.getAllProducts();
        for (Product product : products) {
            tableModel.addRow(new Object[]{product.getId(), product.getName(), product.getPrice(), product.getImageUrl()});
        }
    }

    private void addProduct() {
        String name = JOptionPane.showInputDialog(this, "Enter the product name:");
        String priceStr = JOptionPane.showInputDialog(this, "Enter the price of the item:");
        String url = JOptionPane.showInputDialog(this, "Enter the product URL:");
        String description = JOptionPane.showInputDialog(this, "Enter a description of the product:");
        String stockStr = JOptionPane.showInputDialog(this, "Enter the item stock:");

        try {
            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);
            Product product = new Product(0, name, description, price, stock, url);
            productService.addProduct(product);
            loadProducts();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Prices and inventory must be in double numbers!", "incorrect", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select the product to be modified!", "incorrect", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);
        double price = Double.parseDouble(tableModel.getValueAt(selectedRow, 2).toString());
        String url = (String) tableModel.getValueAt(selectedRow, 3);
        String description = JOptionPane.showInputDialog(this, "Modify the description:", "");
        String stockStr = JOptionPane.showInputDialog(this, "Modify stock:", "");

        String newName = JOptionPane.showInputDialog(this, "Modify the name:", name);
        String newPriceStr = JOptionPane.showInputDialog(this, "Modify the price:", price);
        String newUrl = JOptionPane.showInputDialog(this, "Modify the URL:", url);

        try {
            double newPrice = Double.parseDouble(newPriceStr);
            int newStock = Integer.parseInt(stockStr);
            Product updatedProduct = new Product(id, newName, description, newPrice, newStock, newUrl);
            productService.updateProduct(updatedProduct);
            loadProducts();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Prices and inventory must be in double numbers!", "incorrect", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select the products to be deleted!", "incorrect", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the product?", "validate", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            productService.deleteProduct(id);
            loadProducts();
        }
    }
}


