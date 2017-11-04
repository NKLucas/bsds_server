package edu.neu.zhiyu.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {
    private static final String DBURL = "jdbc:postgresql://" +
            "bsds-postgresql-db.cdz7axjtmano.us-west-2.rds.amazonaws.com:5432/" +
            "SkiDB";
    private static final String USER = "zhiyu";
    private static final String PASSWORD = "nklucas1991";

    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            Logger.getLogger(DBConnection.class.getName())
                    .log(Level.SEVERE, "Failed to register JDBC driver!", e);
        }

        Connection connection = null;

        try {
            System.out.println("DBURL: " + DBURL);
            connection = DriverManager.getConnection(DBURL, USER, PASSWORD);
        } catch (SQLException e) {
            Logger.getLogger(DBConnection.class.getName())
                    .log(Level.SEVERE, "Failed to connect to db.", e);
        }

        return connection;
    }

    public static void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            Logger.getLogger(DBConnection.class.getName())
                    .log(Level.SEVERE, "Failed to close db connection.", e);
        }
    }
}
