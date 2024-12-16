package ui;



import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Locale;

import items.Customer;
import services.CustomerService;

public class CustomerManagementFrame extends JFrame {
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private CustomerService customerService;
    private JTextField searchField;

    public CustomerManagementFrame() {
        customerService = new CustomerService();

        setTitle("customer management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());


        tableModel = new DefaultTableModel(new Object[]{"ID", "name", "email", "phone", "address"}, 0);
        customerTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        add(scrollPane, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Client");
        JButton deleteButton = new JButton("Delete Customer");

        addButton.addActionListener(this::handleAddCustomer);
        deleteButton.addActionListener(this::handleDeleteCustomer);

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // 搜索框和按钮
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(this::handleSearch); // 绑定搜索按钮事件
        searchPanel.add(new JLabel("Search by Name:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.NORTH);

        loadCustomers();

        setLocationRelativeTo(null);
    }

    // Loading customer data
    private void loadCustomers() {
        tableModel.setRowCount(0); // 清空表格数据
        List<Customer> customers = customerService.getAllCustomers();
        for (Customer customer : customers) {
            tableModel.addRow(new Object[]{
                    customer.getId(),
                    customer.getName(),
                    customer.getEmail(),
                    customer.getPhone(),
                    customer.getAddress()
            });
        }
    }

    // Add Client
    private void handleAddCustomer(ActionEvent e) {
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("email:"));
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("phone:"));
        inputPanel.add(phoneField);
        inputPanel.add(new JLabel("address:"));
        inputPanel.add(addressField);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Add New Customer", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            String address = addressField.getText();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be completed!", "incorrect", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Customer newCustomer = new Customer(0, name, email, phone, address);
            if (customerService.addCustomer(newCustomer)) {
                JOptionPane.showMessageDialog(this, "Customer added successfully!");
                loadCustomers();
            } else {
                JOptionPane.showMessageDialog(this, "Customer add failed!", "incorrect", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    // delete customer
    private void handleDeleteCustomer(ActionEvent e) {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a client first!", "incorrect", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int customerId = (int) tableModel.getValueAt(selectedRow, 0); // 获取选中行的客户 ID
        int result = JOptionPane.showConfirmDialog(this, "Confirm deletion of selected customers?", "Confirm deletion", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            if (customerService.deleteCustomer(customerId)) {
                JOptionPane.showMessageDialog(this, "Customer deletion was successful!");
                loadCustomers(); // 重新加载客户数据
            } else {
                JOptionPane.showMessageDialog(this, "Customer deletion failed!", "incorrect", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // search
    private void handleSearch(ActionEvent e) {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadCustomers(); // if null load all
        } else {
            List<Customer> filteredCustomers = customerService.searchCustomersByName(keyword);
            tableModel.setRowCount(0);
            for (Customer customer : filteredCustomers) {
                tableModel.addRow(new Object[]{
                        customer.getId(),
                        customer.getName(),
                        customer.getEmail(),
                        customer.getPhone(),
                        customer.getAddress()
                });
            }
        }
    }







    public static void main(String[] args) {

        // Set the global language to English
        Locale.setDefault(Locale.ENGLISH);

        SwingUtilities.invokeLater(() -> new CustomerManagementFrame().setVisible(true));
    }
}
