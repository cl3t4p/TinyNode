package com.cl3t4p.TinyNode.routes.api;

import com.cl3t4p.TinyNode.Main;
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

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
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


    public void sendMessage(String device_id,String message){
        var pair = sessionMap.getByDeviceID(device_id);
        SimpleDevice device = pair.component1();
        WsContext ctx = pair.component2();

        long random = System.currentTimeMillis();
        String enc_data = device.encrypt(message+random);
        ctx.send(enc_data);
    }


    @Override
    public void handleConnect(@NotNull WsConnectContext wsCnt) throws Exception {
        String str_code = wsCnt.cookie("code");
        var bai = new ByteArrayInputStream(AESTools.decrypt(str_code,Main.getGlobalSecretKey()).getBytes());
        String mac_hex = HexTools.encode(bai.readNBytes(6));
        SimpleDevice device = deviceRepo.getDeviceByID(mac_hex);

        sessionMap.add(device,wsCnt);
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCls) {
        sessionMap.removeBySessionID(wsCls.sessionId());
    }

    @Override
    public void handleBinaryMessage(@NotNull WsBinaryMessageContext wsBinaryMessageContext) {
    }

    @Override
    public void handleError(@NotNull WsErrorContext wsErrorContext) {
        assert wsErrorContext.error() != null;
        LOGGER.error(wsErrorContext.error().getLocalizedMessage());
    }



    private static class MirroredSession{
        HashMap<String, Pair<SimpleDevice,WsContext>> activeSessions = new HashMap<>();
        HashMap<String, Pair<SimpleDevice,WsContext>> mirrorActiveSession = new HashMap<>();


        private void add(SimpleDevice device,WsContext context){
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
