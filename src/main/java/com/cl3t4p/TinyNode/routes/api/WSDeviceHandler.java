package com.cl3t4p.TinyNode.routes.api;

import com.cl3t4p.TinyNode.db.DeviceRepo;
import com.cl3t4p.TinyNode.db.RepoManager;
import com.cl3t4p.TinyNode.model.BaseDevice;
import com.cl3t4p.TinyNode.tools.MirroredSession;
import io.javalin.websocket.*;
import java.nio.channels.ClosedChannelException;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.simple.SimpleLoggerFactory;

public class WSDeviceHandler
    implements WsCloseHandler, WsConnectHandler, WsErrorHandler, WsBinaryMessageHandler {

  private static final Logger LOGGER =
      new SimpleLoggerFactory().getLogger(WSDeviceHandler.class.getSimpleName());

  private static final MirroredSession sessionMap =
      RepoManager.getInstance().getDeviceSessionsMap();

  @Getter private static WSDeviceHandler instance;

  private final DeviceRepo deviceRepo = RepoManager.getInstance().getDeviceRepo();

  public static void getEndpoints(WsConfig wsConfig) {
    instance = new WSDeviceHandler();
    wsConfig.onClose(instance);
    wsConfig.onConnect(instance);
    wsConfig.onError(instance);
    wsConfig.onBinaryMessage(instance);
  }

  /**
   * Handles the WebSocket connection event. This method retrieves the device ID from the connection
   * context's cookies, decrypts it, and adds the device and its associated WebSocket context to the
   * session map.
   */
  @Override
  public void handleConnect(@NotNull WsConnectContext wsCnt) throws Exception {
    LOGGER.info("Device connected");
    String str_code = wsCnt.cookie("code");

    if (str_code == null) {
      LOGGER.info("Device is missing a cookie");
      wsCnt.closeSession();
      return;
    }
    // Get the device ID from the repository
    BaseDevice device = deviceRepo.getDeviceByID(str_code);
    if (device == null) {
      // Close connection if device is not present
      LOGGER.info("Device not found with code {}", str_code);
      wsCnt.closeSession(WsCloseStatus.NORMAL_CLOSURE, "Device not found");
    } else {
      // Add device to the active ones
      sessionMap.add(device, wsCnt);
    }
  }

  @Override
  public void handleClose(@NotNull WsCloseContext wsCls) {
    LOGGER.info("Device disconnected!");
    // sessionMap.getBySessionID(wsCls.sessionId()).component1().getId());
    sessionMap.removeBySessionID(wsCls.sessionId());
  }

  @Override
  public void handleBinaryMessage(@NotNull WsBinaryMessageContext wsBinaryMessageContext) {}

  @Override
  public void handleError(@NotNull WsErrorContext wsErrorContext) {
    assert wsErrorContext.error() != null;
    if (wsErrorContext.error().getClass().equals(ClosedChannelException.class)) {
      LOGGER.warn("Device aborted connection!");
    } else {
      LOGGER.error("Websocket error : ", wsErrorContext.error());
    }
  }
}
