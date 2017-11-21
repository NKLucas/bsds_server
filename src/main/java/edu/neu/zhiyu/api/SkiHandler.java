package edu.neu.zhiyu.api;

import com.google.gson.Gson;
import edu.neu.zhiyu.cache.CacheToDBWorker;
import edu.neu.zhiyu.cache.CacheToSQSWorker;
import edu.neu.zhiyu.cache.DataCacher;
import edu.neu.zhiyu.cache.ServerLatencyCacher;
import edu.neu.zhiyu.db.LiftStatDAO;
import edu.neu.zhiyu.model.HttpRequestType;
import edu.neu.zhiyu.model.LiftStat;
import edu.neu.zhiyu.model.RFIDLiftData;
import edu.neu.zhiyu.model.ServerLatency;
import org.apache.http.protocol.HTTP;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;


@Path("/ski")
public class SkiHandler {

    static {
        CacheToDBWorker.start();
        CacheToSQSWorker.start();
    }

    private Gson gson = new Gson();
    private DataCacher cacher = DataCacher.getInstance();
    private ServerLatencyCacher latencyCacher = ServerLatencyCacher.getInstance();
    private LiftStatDAO liftStatDAO = LiftStatDAO.getInstance();

    @GET
    @Path("/myvert/{skierID}/{day}")
    public String getMyVertStat(@PathParam("skierID") String skierID, @PathParam("day") int day) throws SQLException {
        long start = System.currentTimeMillis();
        int errCount = 0;
        String response;

        try {
            LiftStat stat = liftStatDAO.getStatByIDAndDay(skierID, day);
            response = gson.toJson(stat);
        } catch (Exception e) {
            errCount = 1;
            response = e.getMessage();
        }

        long respTime = System.currentTimeMillis() - start;
        latencyCacher.add(new ServerLatency(HttpRequestType.GET, respTime, errCount));

        return response;
    }

    @POST
    @Path("/load")
    @Consumes(MediaType.APPLICATION_JSON)
    public String storeRecord(String data) {
        long start = System.currentTimeMillis();

        addToCache(data);

        long respTime = System.currentTimeMillis() - start;
        latencyCacher.add(new ServerLatency(HttpRequestType.POST, respTime));

        return "Got!";
    }

    private void addToCache(String data) {
        RFIDLiftData record = gson.fromJson(data, RFIDLiftData.class);
        cacher.add(record);
    }
}
