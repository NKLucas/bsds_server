package edu.neu.zhiyu.api;

import com.google.gson.Gson;
import edu.neu.zhiyu.cache.DataCacher;
import edu.neu.zhiyu.model.RFIDLiftData;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Path("/ski")
public class SkiHandler {
    @GET
    @Path("/myvert/{userID}/{day}")
    public String getMyVertStat(@PathParam("userID") String userId, @PathParam("day") int day) {
        return " " + userId + day;
    }

    @POST
    @Path("/load")
    @Consumes(MediaType.APPLICATION_JSON)
    public String storeRecord(String data) {
//        addToCache(data);
        return "Got!";
    }

    private void addToCache(String data) {
        DataCacher cacher = DataCacher.getInstance();
        Gson gson = new Gson();
        RFIDLiftData record = gson.fromJson(data, RFIDLiftData.class);
        cacher.add(record);
    }

}
