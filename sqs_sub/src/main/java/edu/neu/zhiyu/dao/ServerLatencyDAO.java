package edu.neu.zhiyu.dao;

import edu.neu.zhiyu.db.DBConnection;
import edu.neu.zhiyu.model.ServerLatency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerLatencyDAO {
    private static Logger logger = Logger.getLogger(ServerLatencyDAO.class.getName());
    private static ServerLatencyDAO instance;
    private static String TABLE_NAME = "server_latencies";
    private static String INSERT_QUERY = "INSERT INTO " + TABLE_NAME +
            " (latency, error_count, request_type) " +
            "VALUES (?, ?, ?)";


    public static ServerLatencyDAO getInstance() {
        if (instance == null) {
            instance = new ServerLatencyDAO();
        }
        return instance;
    }

    public static void batchInsert(List<ServerLatency> data) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(INSERT_QUERY);
            for (ServerLatency record : data) {
                prepareStatement(statement, record);
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to batch insert records!", ex);
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    private static void prepareStatement(PreparedStatement statement, ServerLatency record) throws SQLException {
        statement.setLong(1, record.getLatency());
        statement.setInt(2, record.getErrorCount());
        statement.setString(3, record.getRequestType().toString());
    }

//    public static void main(String[] args) throws SQLException {
//        List<ServerLatency> data = new ArrayList<>();
//        data.add(new ServerLatency("1", 2, "3", "4", "5"));
//        batchInsert(data);
//    }


}
