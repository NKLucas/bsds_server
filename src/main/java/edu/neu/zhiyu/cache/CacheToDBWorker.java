package edu.neu.zhiyu.cache;

import edu.neu.zhiyu.db.LiftDataDAO;
import edu.neu.zhiyu.db.LiftStatDAO;
import edu.neu.zhiyu.model.LiftStat;
import edu.neu.zhiyu.model.RFIDLiftData;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CacheToDBWorker {

    private static Logger logger = Logger.getLogger(CacheToDBWorker.class.getName());
    private static ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(1);
    private static LiftDataDAO liftDataDAO;
    private static LiftStatDAO liftStatDAO;
    private static long DELAY = 0;          // no delay
    private static long PERIOD = 5000;      // 5 seconds

    public static void start() {
        logger.log(Level.INFO, "CacheToDBWorker Started!");
        liftDataDAO = LiftDataDAO.getInstance();
        liftStatDAO = LiftStatDAO.getInstance();

        scheduledService.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        insertCachedDataToDB();
                    }
                }, DELAY, PERIOD, TimeUnit.MILLISECONDS);
    }

    private static void insertCachedDataToDB() {
        DataCacher cacher = DataCacher.getInstance();
        if (cacher.size() > 0) {
            List<RFIDLiftData> dataList = cacher.getCachedData();
            try {
                liftDataDAO.batchInsert(dataList);
                liftStatDAO.batchUpsert(dataList);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Worker failed to insert/update batch!", e);
            }
        }
    }

}
