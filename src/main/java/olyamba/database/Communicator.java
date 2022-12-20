package olyamba.database;

import java.sql.*;

public class Communicator {
    private static final String DB_URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("USER");
    private static final String PASS = System.getenv("PASS");
    private static Connection connection;
    private static Statement statement;
    private static Customers customers;
    private static Products products;

    public void start() throws SQLException {
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement();
            customers = new Customers(connection);
            customers.createCustomers();
            products = new Products(connection);
            products.createProducts();
        } catch (SQLException e) {
            System.out.println("Проблемы с подключением к базе данных.");
            e.printStackTrace();
            System.exit(0);
        }
        if (connection != null) {
            System.out.println("Подключение к базе данных прошло успешно.");
        } else {
            System.out.println("Не удалось подключиться к базе данных.");
        }
    }

    public static Customers getCustomers() {
        return customers;
    }

    public static Products getProducts() {
        return products;
    }
}

