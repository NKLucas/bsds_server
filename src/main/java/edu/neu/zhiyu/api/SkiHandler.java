package edu.neu.zhiyu.api;

import com.google.gson.Gson;
import edu.neu.zhiyu.cache.CacheToDBWorker;
import edu.neu.zhiyu.cache.DataCacher;
import edu.neu.zhiyu.db.LiftStatDAO;
import edu.neu.zhiyu.model.LiftStat;
import edu.neu.zhiyu.model.RFIDLiftData;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;


@Path("/ski")
public class SkiHandler {

    static {
        CacheToDBWorker.start();
    }

    private Gson gson = new Gson();
    private DataCacher cacher = DataCacher.getInstance();
    private LiftStatDAO liftStatDAO = LiftStatDAO.getInstance();

    @GET
    @Path("/myvert/{skierID}/{day}")
    public String getMyVertStat(@PathParam("skierID") String skierID, @PathParam("day") int day) throws SQLException {
        LiftStat stat = liftStatDAO.getStatByIDAndDay(skierID, day);
        return gson.toJson(stat);
    }

    @POST
    @Path("/load")
    @Consumes(MediaType.APPLICATION_JSON)
    public String storeRecord(String data) {
        addToCache(data);
        return "Got!";
    }

    private void addToCache(String data) {
        RFIDLiftData record = gson.fromJson(data, RFIDLiftData.class);
        cacher.add(record);
    }

}
