package com.cl3t4p.TinyNode.routes;

import com.cl3t4p.TinyNode.db.DeviceRepo;
import com.cl3t4p.TinyNode.db.RepoManager;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

public class DevicesRoute {
    private static DeviceRepo deviceRepo = RepoManager.getInstance().getDeviceRepo();





    public static void getDevices(@NotNull Context ctx){


    }
}
