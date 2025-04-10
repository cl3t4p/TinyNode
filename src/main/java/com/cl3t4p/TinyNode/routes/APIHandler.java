package com.cl3t4p.TinyNode.routes;



import com.cl3t4p.TinyNode.db.RepoManager;
import com.cl3t4p.TinyNode.routes.api.WSDeviceHandler;
import io.javalin.apibuilder.EndpointGroup;


import static io.javalin.apibuilder.ApiBuilder.*;

public class APIHandler {


    private static final String VERSION = "/v01";


    public static EndpointGroup getEndpoints(){
        return ()-> path(VERSION,()->{
        //path("/info",IPHandler::getEndpoints);
        path("/device",()->{
            get("/com/devices",ctx -> ctx.json(RepoManager.getInstance().getDeviceRepo().getAllDevices()));
            ws("/com",WSDeviceHandler::getEndpoints);
            });
    });
    }
}
