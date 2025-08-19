package com.cl3t4p.TinyNode.routes.api;

import com.cl3t4p.TinyNode.db.DeviceRepo;
import com.cl3t4p.TinyNode.db.RepoManager;
import com.cl3t4p.TinyNode.model.BaseDevice;
import com.cl3t4p.TinyNode.model.CommandRequest;
import com.cl3t4p.TinyNode.tools.MirroredSession;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import java.sql.SQLException;
import org.jetbrains.annotations.NotNull;

public class CommandHandler implements Handler {

  private final DeviceRepo deviceRepo = RepoManager.getInstance().getDeviceRepo();
  private final MirroredSession deviceMirroredSession =
      RepoManager.getInstance().getDeviceSessionsMap();

  @Override
  public void handle(@NotNull Context ctx) throws SQLException {
    CommandRequest cmd_request = ctx.bodyAsClass(CommandRequest.class);
    String device_id = cmd_request.getDeviceId();
    BaseDevice device = deviceRepo.getDeviceByID(device_id);
    if (device == null) {
      ctx.status(404);
      return;
    }
    var pair = deviceMirroredSession.getByDeviceID(device_id);
    if (pair == null) {
      ctx.status(404);
      return;
    }
    byte[] encrypted_request = pair.component1().encryptCommandRequest(cmd_request);
    pair.component2().send(encrypted_request);
  }
}
