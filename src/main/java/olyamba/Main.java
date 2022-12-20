package olyamba;

import olyamba.bot.Bot;
import olyamba.database.Communicator;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        Bot bot = new Bot();
        Communicator communicator = new Communicator();
        communicator.start();
        bot.serve();
    }
}