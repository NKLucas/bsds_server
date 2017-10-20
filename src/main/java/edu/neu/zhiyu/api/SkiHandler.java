package edu.neu.zhiyu.api;

import com.google.gson.Gson;
import edu.neu.zhiyu.model.SkiRecord;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Path("/ski")
public class SkiHandler {
    @GET
    @Path("/myvert/{userID}/{day}")
    public String getMyVertStat(@PathParam("userID") String userId, @PathParam("day") int day) {
        return userId + day;
    }

    @POST
    @Path("/load")
    @Consumes(MediaType.APPLICATION_JSON)
    public String storeRecord(String data) {
        Gson gson = new Gson();
        SkiRecord record = gson.fromJson(data, SkiRecord.class);
        return gson.toJson(record);
    }

}
