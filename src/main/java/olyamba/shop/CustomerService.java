package olyamba.shop;

import olyamba.database.Communicator;

import java.sql.SQLException;

public class CustomerService {
    public static void addCustomer(String name, String mail) {
        try {
            if (!Communicator.getCustomers().checkCustomer(name)) {
                Communicator.getCustomers().addCustomer(name, mail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
