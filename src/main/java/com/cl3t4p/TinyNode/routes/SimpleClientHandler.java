package com.cl3t4p.TinyNode.routes;

import io.javalin.http.Context;

import org.jetbrains.annotations.NotNull;


public class SimpleClientHandler {


    public static void handleDevicesRequest(@NotNull Context ctx) {
        String device = ctx.pathParam("device");
        String body = ctx.body();

    }


}
