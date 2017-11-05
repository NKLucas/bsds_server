package edu.neu.zhiyu.db;

import edu.neu.zhiyu.model.RFIDLiftData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LiftDataDAO {

    private static Logger logger = Logger.getLogger(LiftDataDAO.class.getName());
    private static LiftDataDAO instance;
    private static String TABLE_NAME = "ski_records";
    private static String INSERT_QUERY = "INSERT INTO " + TABLE_NAME +
            " (resort_id, day_num, skier_id, lift_id, time) " +
            "VALUES (?, ?, ?, ?, ?)";


    public static LiftDataDAO getInstance() {
        if (instance == null) {
            instance = new LiftDataDAO();
        }
        return instance;
    }

    public static void batchInsert(List<RFIDLiftData> data) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = null;
        try{
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(INSERT_QUERY);
            for (RFIDLiftData record : data) {
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

    private static void prepareStatement(PreparedStatement statement, RFIDLiftData record) throws SQLException {
        statement.setString(1, record.getResortID());
        statement.setInt(2, record.getDayNum());
        statement.setString(3, record.getSkierID());
        statement.setString(4, record.getLiftID());
        statement.setString(5, record.getTime());
    }

//    public static void main(String[] args) throws SQLException {
//        List<RFIDLiftData> data = new ArrayList<>();
//        data.add(new RFIDLiftData("1", 2, "3", "4", "5"));
//        batchInsert(data);
//    }


}
