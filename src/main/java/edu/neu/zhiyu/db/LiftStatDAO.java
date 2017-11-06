package edu.neu.zhiyu.db;

import edu.neu.zhiyu.model.LiftStat;
import edu.neu.zhiyu.model.RFIDLiftData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LiftStatDAO {
    private static Logger logger = Logger.getLogger(LiftDataDAO.class.getName());
    private static String TABLE_NAME = "ski_stats";
    private static int[] VERTICAL = {200, 300, 400, 500};
    private static LiftStatDAO instance;

    private static String UPSERT_QUERY =
                    "INSERT INTO " + TABLE_NAME + " (skier_id, day_num, lift_count, total_vertical) " +
                    "VALUES(?,?,?,?) " +
                    "ON CONFLICT (skier_id, day_num) DO UPDATE SET " +
                    "total_vertical = " + TABLE_NAME + ".total_vertical + EXCLUDED.total_vertical, " +
                    "lift_count = " + TABLE_NAME+ ".lift_count + EXCLUDED.lift_count";


    private static String GET_QUERY = "SELECT * FROM " + TABLE_NAME + " WHERE skier_id = ? AND day_num = ?";

    public static LiftStatDAO getInstance() {
        if (instance == null) {
            instance = new LiftStatDAO();
        }
        return instance;
    }

    public LiftStat getStatByIDAndDay(String skierID, int dayNum) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = null;
        LiftStat stat = new LiftStat();
        try {
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(GET_QUERY);
            statement.setString(1, skierID);
            statement.setInt(2, dayNum);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                stat.setSkierID(skierID);
                stat.setDayNum(dayNum);
                stat.setLiftCount(resultSet.getInt("lift_count"));
                stat.setTotalVertical(resultSet.getInt("total_vertical"));
            }
            statement.close();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to get the stat for " + skierID + " at day " + dayNum);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return stat;
    }

    public void batchUpsert(List<RFIDLiftData> data) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = null;
        try{
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(UPSERT_QUERY);
            for (RFIDLiftData record : data) {
                prepareStatement(statement, record);
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to batch upsert lift stat records!", ex);
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
        int index = (Integer.parseInt(record.getLiftID()) - 1) / 10;
        statement.setString(1, record.getSkierID());
        statement.setInt(2, record.getDayNum());
        statement.setInt(3, 1);
        statement.setInt(4, VERTICAL[index]);
    }

}
