package edu.neu.zhiyu.db;

import edu.neu.zhiyu.model.RFIDLiftData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LiftStatDAO {
    private static Logger logger = Logger.getLogger(LiftDataDAO.class.getName());
    private static String TABLE_NAME = "ski_stats";
    private static int[] VERTICAL = {200, 300, 400, 500};
    private static LiftStatDAO instance;

    private static final String UPSERT_QUERY =
            "INSERT INTO " + TABLE_NAME + "(skier_id, day_num, "
                    + "lift_count, total_vertical) VALUES(?,?,?,?) "
                    + "ON CONFLICT (skier_id, day_num) DO UPDATE SET "
                    + "total_vertical = " + TABLE_NAME + ".total_vertical + EXCLUDED.total_vertical, "
                    + "lift_count = " + TABLE_NAME+ ".lift_count + EXCLUDED.lift_count";


    public static LiftStatDAO getInstance() {
        if (instance == null) {
            instance = new LiftStatDAO();
        }
        return instance;
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
