package olyamba.database;

import java.sql.*;
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
                if (rs.getInt(2) > 0) {
                    return true;
                }
        return false;
    }

    public void removeProduct(String product) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT english_name, quantity FROM products",
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        ResultSet rs = stmt.executeQuery();
        int currentQuantity = 999;
        while (rs.next()) {
            if (product.equals(rs.getString(1))) {
                currentQuantity = rs.getInt(2);
                currentQuantity--;
                break;
            }
        }
        String sql = "UPDATE products SET quantity = " + currentQuantity + " WHERE english_name = '" + product + "'";
        statement.executeUpdate(sql);
    }
}
