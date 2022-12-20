package olyamba.shop;

import olyamba.database.Communicator;
import olyamba.database.Products;

import java.sql.SQLException;
import java.util.Map;

public class ProductsService {
    public static Map<String, String> getProducts() {
        try {
            return Communicator.getProducts().getAllProducts();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean order(String product) {
        try {
            boolean success = Communicator.getProducts().isAvailable(product);
            //if (!success) return false;
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
