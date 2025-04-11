package com.cl3t4p.TinyNode.routes.api;

import com.cl3t4p.TinyNode.TinyNode;
import com.cl3t4p.TinyNode.db.DeviceRepo;
import com.cl3t4p.TinyNode.db.RepoManager;
import com.cl3t4p.TinyNode.devices.SimpleDevice;
import com.cl3t4p.TinyNode.tools.AESTools;
import com.cl3t4p.TinyNode.tools.HexTools;
import io.javalin.websocket.*;
import kotlin.Pair;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.simple.SimpleLoggerFactory;

import javax.crypto.IllegalBlockSizeException;
import java.io.ByteArrayInputStream;
import java.nio.channels.ClosedChannelException;
import java.util.HashMap;


public class WSDeviceHandler implements WsCloseHandler, WsConnectHandler, WsErrorHandler,WsBinaryMessageHandler {

    private static final Logger LOGGER = new SimpleLoggerFactory().getLogger(WSDeviceHandler.class.getSimpleName());

    @Getter
    private static WSDeviceHandler instance;

    private final DeviceRepo deviceRepo = RepoManager.getInstance().getDeviceRepo();
    private final MirroredSession sessionMap = new MirroredSession();


    public static void getEndpoints(WsConfig wsConfig) {
        instance = new WSDeviceHandler();
        wsConfig.onClose(instance);
        wsConfig.onConnect(instance);
        wsConfig.onError(instance);
        wsConfig.onBinaryMessage(instance);
    }


    /**
     * Sends a message to the device with the specified device ID.
     * This method retrieves the device and its associated WebSocket context from the session map,
     * encrypts the message using the device's encryption method, and sends it through the WebSocket context.
     * @param device_id The ID of the device to send the message to.
     * @param message The message to be sent to the device.
     */
    public void sendMessage(String device_id,String message){
        var pair = sessionMap.getByDeviceID(device_id);
        SimpleDevice device = pair.component1();
        WsContext ctx = pair.component2();

        long random = System.currentTimeMillis();
        String enc_data = device.encrypt(message+random);
        ctx.send(enc_data);
    }


    /**
     * Handles the WebSocket connection event.
     * This method retrieves the device ID from the connection context's cookies,
     * decrypts it, and adds the device and its associated WebSocket context to the session map.
     */
    @Override
    public void handleConnect(@NotNull WsConnectContext wsCnt) throws Exception {
        //Get the encrypted device ID from the cookies

        String str_code = wsCnt.cookie("code");
        if(str_code == null){
            wsCnt.closeSession();
            return;
        }
        try(var bai = new ByteArrayInputStream(AESTools.decryptToByteFromBase64(str_code, TinyNode.getGlobalSecretKey()));) {

            String mac_hex = HexTools.encode(bai.readNBytes(6));
            LOGGER.info("New device connected: {}", mac_hex);

            //Get the device ID from the repository
            SimpleDevice device = deviceRepo.getDeviceByID(mac_hex);


            if (device == null){
                //New device logic here
                device = new SimpleDevice(mac_hex);
                device.setName(mac_hex);
                deviceRepo.addDevice(device);
            }

            sessionMap.add(device,wsCnt);
        }catch (IllegalBlockSizeException e){
            wsCnt.closeSession();
            LOGGER.warn("Client connect error",e);
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCls) {
        LOGGER.info("Device {} disconnected!",sessionMap.getBySessionID(wsCls.sessionId()).component1().getId());
        sessionMap.removeBySessionID(wsCls.sessionId());
    }

    @Override
    public void handleBinaryMessage(@NotNull WsBinaryMessageContext wsBinaryMessageContext) {}

    @Override
    public void handleError(@NotNull WsErrorContext wsErrorContext) {
        assert wsErrorContext.error() != null;
        if(wsErrorContext.error().getClass().equals(ClosedChannelException.class)){
            LOGGER.warn("Device {} aborted connection!",sessionMap.getBySessionID(wsErrorContext.sessionId()).component1().getId());
        }else{
            LOGGER.error("Websocket error : ",wsErrorContext.error());
        }
    }




    /**
     * MirroredSession - A class that manages the mapping between device IDs and their associated WebSocket contexts.
     * It provides methods to add, remove, and retrieve devices and their contexts based on device ID or session ID.
     */
    private static class MirroredSession{
        HashMap<String, Pair<SimpleDevice,WsContext>> activeSessions = new HashMap<>();
        HashMap<String, Pair<SimpleDevice,WsContext>> mirrorActiveSession = new HashMap<>();


        /**
         * Adds a device and its associated WebSocket context to the session map.
         * @param device The SimpleDevice object to be added.
         * @param context The WebSocket context associated with the device.
         */
        private void add(@NotNull SimpleDevice device, WsContext context){
            var pair = new Pair<>(device,context);
            activeSessions.put(device.getId(),pair);
            mirrorActiveSession.put(context.sessionId(),pair);
        }

        private void removeBySessionID(String session_id){
            String device_id = mirrorActiveSession.remove(session_id).component1().getId();
            activeSessions.remove(device_id);
        }

        private void removeByDeviceID(String device_id){
            String session_id = activeSessions.remove(device_id).component1().getId();
            mirrorActiveSession.remove(session_id);
        }


        private Pair<SimpleDevice,WsContext> getByDeviceID(String device_id){
            return activeSessions.get(device_id);
        }

        private Pair<SimpleDevice,WsContext> getBySessionID(String session_id){
            return mirrorActiveSession.get(session_id);
        }
    }


}
