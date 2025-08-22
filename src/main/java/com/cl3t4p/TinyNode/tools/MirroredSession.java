package com.cl3t4p.TinyNode.tools;

import com.cl3t4p.TinyNode.model.BaseDevice;
import com.cl3t4p.TinyNode.model.CommandRequest;
import io.javalin.websocket.WsContext;
import java.util.HashMap;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;

/**
 * MirroredSession - A class that manages the mapping between device IDs and their associated
 * WebSocket contexts. It provides methods to add, remove, and retrieve devices and their contexts
 * based on device ID or session ID.
 */
public class MirroredSession {
  HashMap<String, Pair<BaseDevice, WsContext>> activeSessions = new HashMap<>();
  HashMap<String, Pair<BaseDevice, WsContext>> mirrorActiveSession = new HashMap<>();
  HashMap<String, CommandRequest> queue_requests = new HashMap<>();

  /**
   * Add commandrequest for the device that is disconected and will reconect
   *
   * @param commandRequest cmd_reuqest that will be added to the queue
   * @param device_id id of the device
   */
  public void addQueueCommandRequest(@NotNull CommandRequest commandRequest, String device_id) {
    queue_requests.put(device_id, commandRequest);
  }

  /**
   * Adds a device and its associated WebSocket context to the session map.
   *
   * @param device The SimpleDevice object to be added.
   * @param context The WebSocket context associated with the device.
   */
  public void add(@NotNull BaseDevice device, WsContext context) {
    var pair = new Pair<>(device, context);
    activeSessions.put(device.getId(), pair);
    mirrorActiveSession.put(context.sessionId(), pair);

    // Check for queue commandreuqest that was sent when the device was not connected
    if (queue_requests.containsKey(context.sessionId())) {
      CommandRequest commandRequest = queue_requests.remove(context.sessionId());
      context.send(device.encryptCommandRequest(commandRequest));
    }
  }

  public void removeBySessionID(String session_id) {

    var device = mirrorActiveSession.remove(session_id);
    if (device != null) {
      activeSessions.remove(device.component1().getId());
    }
  }

  public void removeByDeviceID(String device_id) {
    String session_id = activeSessions.remove(device_id).component1().getId();
    mirrorActiveSession.remove(session_id);
  }

  public Pair<BaseDevice, WsContext> getByDeviceID(String device_id) {
    return activeSessions.get(device_id);
  }

  public Pair<BaseDevice, WsContext> getBySessionID(String session_id) {
    return mirrorActiveSession.get(session_id);
  }
}
