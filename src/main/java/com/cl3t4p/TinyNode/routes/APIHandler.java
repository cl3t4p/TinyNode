package com.cl3t4p.TinyNode.routes;

import static io.javalin.apibuilder.ApiBuilder.*;

import com.cl3t4p.TinyNode.db.RepoManager;
import com.cl3t4p.TinyNode.routes.api.CommandHandler;
import com.cl3t4p.TinyNode.routes.api.WSDeviceHandler;
import io.javalin.apibuilder.EndpointGroup;

public class APIHandler {

  private static final String VERSION = "/v01";

  public static EndpointGroup getEndpoints() {
    return () ->
        path(
            VERSION,
            () -> {
              // path("/info",IPHandler::getEndpoints);
              path(
                  "/device",
                  () -> {
                    get(
                        "/com/devices",
                        ctx -> ctx.json(RepoManager.getInstance().getDeviceRepo().getAllDevices()));
                    ws("/com", WSDeviceHandler::getEndpoints);
                    post("/command", new CommandHandler());
                  });
            });
  }
}
