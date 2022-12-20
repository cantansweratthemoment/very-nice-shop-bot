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
                " mail TEXT)";
        statement.execute(createTableSQL);
    }

    public void addCustomer(String name, String mail) throws SQLException {
        String sql = "INSERT INTO customers (name, mail) VALUES (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, mail);
        preparedStatement.execute();
    }

    public boolean checkCustomer(String name) throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT name FROM customers");
        while (rs.next())
            if (name.equals(rs.getString(1)))
                return true;
        return false;
    }
}
