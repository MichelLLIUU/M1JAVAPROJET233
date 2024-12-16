package ui;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class MainMenuFrame extends JFrame {
    public MainMenuFrame() {
        setTitle("main menu");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 1, 10, 10)); // 5 行按钮，间距 10px

        // the buttons

        JButton customerButton = new JButton("customer management");
        customerButton.addActionListener(e -> new CustomerManagementFrame().setVisible(true));
        add(customerButton);


        JButton productButton = new JButton("product management");
        productButton.addActionListener(e -> new ProductManagementFrame().setVisible(true));
        add(productButton);


        JButton cartButton = new JButton("Cart Management");
        cartButton.addActionListener(e -> new CartManagementFrame().setVisible(true));
        add(cartButton);


        JButton orderButton = new JButton("Order Management");
        orderButton.addActionListener(e -> new OrderManagementFrame().setVisible(true));
        add(orderButton);


        JButton factureButton = new JButton("facture management");
        factureButton.addActionListener(e -> new FactureManagementFrame().setVisible(true));
        add(factureButton);

        // Setting the interface to be centred
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {

        // Set the global language to English
        Locale.setDefault(Locale.ENGLISH);

        SwingUtilities.invokeLater(() -> new MainMenuFrame().setVisible(true));
    }
}
