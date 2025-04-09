package com.cl3t4p.TinyNode.routes;



import com.cl3t4p.TinyNode.routes.api.WSDeviceHandler;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Handler;


import static io.javalin.apibuilder.ApiBuilder.*;

public class APIHandler {


    private static final String VERSION = "/v01";


    public static EndpointGroup getEndpoints(){
        return ()-> path(VERSION,()->{
        //path("/info",IPHandler::getEndpoints);
        path("/device",()->{
            ws("/com/{device_id}",WSDeviceHandler::getEndpoints);
            });
    });
    }
}
