package olyamba.shop;

import olyamba.database.Communicator;

import java.sql.SQLException;

public class CustomerService {
    public static void addCustomer(String name, String mail, String city) {
        try {
            if (!Communicator.getCustomers().checkCustomer(name)) {
                Communicator.getCustomers().addCustomer(name, mail, city);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkCustomer(String name) {
        try {
            return Communicator.getCustomers().checkCustomer(name);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void deleteCustomer(String name) {
        try {
            Communicator.getCustomers().deleteCustomer(name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getCustomerInfo(String name) {
        try {
            return Communicator.getCustomers().getCustomerInfo(name);
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }
}
