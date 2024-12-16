package dataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

public class DataBaseInitialize {
    private static final String URL = "jdbc:mysql://localhost:3306/"; // Database URL
    private static final String USER = "root"; // Replace with your MySQL username
    private static final String PASSWORD = "123456"; // Replace with your MySQL password
    private static final String DATABASE_NAME = "CarshopNew1";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {


            String createDatabase = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            statement.executeUpdate(createDatabase);
            System.out.println("Database has been created or exists: " + DATABASE_NAME);

            // Switch to the new database
            String useDatabase = "USE " + DATABASE_NAME;
            statement.executeUpdate(useDatabase);


            createTables(statement);


            insertInitialDataIfEmpty(statement);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createTables(Statement statement) throws Exception {

        String productsTable = """
                CREATE TABLE IF NOT EXISTS products (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    description TEXT,
                    price DOUBLE NOT NULL,
                    stock INT NOT NULL,
                    image_url VARCHAR(2083)
                );
                """;


        String usersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(255) NOT NULL UNIQUE,
                    password VARCHAR(255) NOT NULL,
                    role ENUM('admin', 'worker') DEFAULT 'worker'
                );
                """;


        String customersTable = """
                CREATE TABLE IF NOT EXISTS customers (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    email VARCHAR(255),
                    phone VARCHAR(50),
                    address TEXT
                );
                """;


        String ordersTable = """
                CREATE TABLE IF NOT EXISTS orders (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    customer_id INT NOT NULL,
                    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    total_amount DOUBLE NOT NULL,
                    status VARCHAR(50) DEFAULT 'on going',
                    FOREIGN KEY (customer_id) REFERENCES customers(id)
                );
                """;


        String orderItemsTable = """
                CREATE TABLE IF NOT EXISTS order_items (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    order_id INT NOT NULL,
                    product_id INT NOT NULL,
                    quantity INT NOT NULL,
                    price DOUBLE NOT NULL,
                    FOREIGN KEY (order_id) REFERENCES orders(id),
                    FOREIGN KEY (product_id) REFERENCES products(id)
                );
                """;


        String cartItemsTable = """
                CREATE TABLE IF NOT EXISTS cart_items (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    product_id INT NOT NULL,
                    product_name VARCHAR(255) NOT NULL,
                    price DOUBLE NOT NULL,
                    quantity INT NOT NULL DEFAULT 1
                );
                """;


        String facturesTable = """
                CREATE TABLE IF NOT EXISTS factures (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    order_id INT NOT NULL,
                    customer_id INT NOT NULL,
                    total_amount DOUBLE NOT NULL,
                    issue_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (order_id) REFERENCES orders(id),
                    FOREIGN KEY (customer_id) REFERENCES customers(id)
                );
                """;


        statement.executeUpdate(productsTable);
        statement.executeUpdate(usersTable);
        statement.executeUpdate(customersTable);
        statement.executeUpdate(ordersTable);
        statement.executeUpdate(orderItemsTable);
        statement.executeUpdate(cartItemsTable);
        statement.executeUpdate(facturesTable);

        System.out.println("All forms have been successfully created!");
    }

    private static void insertInitialDataIfEmpty(Statement statement) throws Exception {
        // Check if the products table is empty
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM products");
        if (resultSet.next() && resultSet.getInt(1) == 0) {
            String insertProducts = """
                INSERT INTO products (name, description, price, stock, image_url)
                VALUES
                ('BMW iX', 'SUV', 84250.0, 10, 'https://www.bmw.ch/content/dam/bmw/marketCH/bmw_ch/common/topics/offers-and-services/cosybev/iX-xDrive40_890x501.jpg'),
                ('BMW X1', 'SUV', 43700.0, 20, 'https://www.horizon.fr//bmw-neuve/images/grande/4830_1.jpg'),
                ('BMW S5', 'Berline', 62850.0, 5, 'https://www.horizon.fr/images/gamme/bmw-serie-5-touring.jpg'),
                ('BMW S7', 'Berline', 121200.0, 5, 'https://www.neubauer-bmw.fr/images/article/vignette/moyenne/bmw-serie-7-berline_23.jpg'),
                ('BMW i4', 'Coupe', 57600.0, 5, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSPOzNxJiawgxss9qeWvMZjgkPBK4WkAZ37aqzyYaP6I0Ne8Qgqraed6AQpVsjr2SLDUaI&usqp=CAU'),
                ('Audi A6', 'E-TRON', 66420.0, 5, 'https://cdn.wheel-size.com/thumbs/b1/c8/b1c842a403d55e612eacbfa61e016016.jpg'),
                ('Audi Q4', 'E-TRON', 46900.0, 5, 'https://mediaservice.audi.com/media/cdb/data/99297049-b9cb-4235-9e16-35de327962b7.jpg')
                ON DUPLICATE KEY UPDATE id=id;
                """;
            statement.executeUpdate(insertProducts);
            System.out.println("The product data was inserted successfully!");
        }


        resultSet = statement.executeQuery("SELECT COUNT(*) FROM users");
        if (resultSet.next() && resultSet.getInt(1) == 0) {
            String insertUsers = """
                INSERT INTO users (username, password, role)
                VALUES
                ('admin','ltianyi765', 'admin')
                ON DUPLICATE KEY UPDATE username=username;
                """;
            statement.executeUpdate(insertUsers);
            System.out.println("User data inserted successfully!");
        }


        resultSet = statement.executeQuery("SELECT COUNT(*) FROM customers");
        if (resultSet.next() && resultSet.getInt(1) == 0) {
            String insertCustomers = """
                INSERT INTO customers (name, email, phone, address)
                VALUES
                ('Alice Johnson', 'alice@163.com', '0148035517', '123 Main St, New York'),
                ('Alex Jeo', 'aljeoex@gmail.com', '0789653748', '5 Rue Felix Pyat, Puteaux'),
                ('Michel LIU', 'ltianyi123@gmail.com', '0748290047', '47 Rue des cevennes, Paris'),
                ('Yuxun CHU', 'wobujioawei1@qq.com', '15027788399', 'Shijiazhuang 345, China'),
                ('Long NAI', 'nibushinailong77@163.com', '13980574819', '21 Buzhidao Xinjiang, China')
                ON DUPLICATE KEY UPDATE name=name; 
                """;
            statement.executeUpdate(insertCustomers);
            System.out.println("Customer data insertion was successful!");
        }
    }
}

