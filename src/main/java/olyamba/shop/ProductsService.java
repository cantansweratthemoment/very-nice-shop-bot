package olyamba.shop;

import olyamba.database.Communicator;
import olyamba.database.Products;
import olyamba.utills.Producer;

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
            if (!success) return false;
            Communicator.getProducts().removeProduct(product);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
