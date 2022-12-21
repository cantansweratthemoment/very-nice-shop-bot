package olyamba.database;

import java.sql.*;

public class Customers {
    private final Statement statement;
    private final Connection connection;

    public Customers(Connection connection) throws SQLException {
        this.connection = connection;
        this.statement = connection.createStatement();
    }

    protected void createCustomers() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS  customers " +
                "(name TEXT, " +
                " mail TEXT, " +
                "city TEXT)";
        statement.execute(createTableSQL);
    }

    public void addCustomer(String name, String mail, String city) throws SQLException {
        String sql = "INSERT INTO customers (name, mail, city) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, mail);
        preparedStatement.setString(3, city);
        preparedStatement.execute();
    }

    public boolean checkCustomer(String name) throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT name FROM customers");
        while (rs.next())
            if (name.equals(rs.getString(1)))
                return true;
        return false;
    }

    public void deleteCustomer(String name) throws SQLException {
        String sql = "DELETE FROM customers WHERE name = '" + name + "'";
        statement.execute(sql);
    }

    public String getCustomerInfo(String name) throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT * FROM customers WHERE name = '" + name + "'");
        while (rs.next())
            return "Ваше имя: " + rs.getString(1) + "\n" + "Ваша почта: " + rs.getString(2) + "\n" + "Ваш город: " + rs.getString(3) + "";
        return "";
    }
}
