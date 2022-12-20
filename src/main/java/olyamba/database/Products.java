package olyamba.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class Products {
    private final Statement statement;
    private final Connection connection;

    public Products(Connection connection) throws SQLException {
        this.connection = connection;
        this.statement = connection.createStatement();
    }

    protected void createProducts() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS products " +
                "(name TEXT, " +
                " english_name TEXT, " +
                " quantity INT)";
        statement.execute(createTableSQL);
    }

    public Map<String, String> getAllProducts() throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT name, english_name FROM products");
        Map<String, String> products = new HashMap<>();
        while (rs.next()) {
            products.put(rs.getString(1), rs.getString(2));
        }
        return products;
    }

    public boolean isAvailable(String product) throws SQLException {
        ResultSet rs = statement.executeQuery("SELECT english_name, quantity FROM products");
        while (rs.next())
            if (product.equals(rs.getString(1)))
                if (rs.getInt(2)>0) {
                    return true;
                }
        return false;
    }
}
