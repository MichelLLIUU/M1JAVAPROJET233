package ui;

import services.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Locale;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Login system");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // User name labels and input boxes
        JLabel usernameLabel = new JLabel("user ID:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1;
        add(usernameField, gbc);

        // Password labels and input boxes
        JLabel passwordLabel = new JLabel("password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        add(passwordField, gbc);

        // log in button
        JButton loginButton = new JButton("log in");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        loginButton.addActionListener(this::handleLogin);

        setLocationRelativeTo(null);
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        UserService userService = new UserService();
        if (userService.validateUser(username, password)) {
            JOptionPane.showMessageDialog(this, "Log in successfully!");
            this.dispose();
            // Jump to the main screen
            new MainMenuFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "The username or password is incorrect!", "incorrect", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {

        // Set the global language to English
        Locale.setDefault(Locale.ENGLISH);

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}

