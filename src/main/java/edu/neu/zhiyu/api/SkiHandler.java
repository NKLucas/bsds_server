package edu.neu.zhiyu.api;

import edu.neu.zhiyu.model.RFIDLiftData;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Path("/ski")
public class SkiHandler {
    @GET
    @Path("/myvert/{userID}/{day}")
    public String getMyVertStat(@PathParam("userID") int userId, @PathParam("day") int day) {
        return " " + userId + day;
    }

    @POST
    @Path("/load")
    @Consumes(MediaType.APPLICATION_JSON)
    public String storeRecord(String data) {
        return "Got!";
    }

}
