package com.cl3t4p.TinyNode;

import com.cl3t4p.TinyNode.config.ConfigFile;
import com.cl3t4p.TinyNode.config.ConfigManager;
import com.cl3t4p.TinyNode.db.RepoManager;
import com.cl3t4p.TinyNode.tools.AESTools;
import com.cl3t4p.TinyNode.tools.HexTools;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


public class WebSocketTest {



    private static String API_BASE_PATH = "/api/v01";
    private static ConfigFile cfg;

    @Before
    @SneakyThrows
    public void setup() {
        cfg = ConfigManager.init(new File("config/config.json")).getConfig();
    }


    @Test
    public void testWebSocketEcho() throws Exception {
        CompletableFuture<String> receivedMessage = new CompletableFuture<>();

        String mac_address = "5cb6037f7fa6";
        HttpClient client = HttpClient.newHttpClient();
        long random = System.currentTimeMillis();
        var bao = ByteBuffer.allocate(6+Long.BYTES);
        bao.put(HexTools.decode(mac_address));
        bao.putLong(random);

        String code = AESTools.encryptToBase64FromByte(bao.array(),cfg.getShared_key());

        WebSocket webSocket = client.newWebSocketBuilder()
                .header("Cookie","code="+code)
                .buildAsync(URI.create("ws://localhost:7070"+API_BASE_PATH+"/device/com"), new WebSocket.Listener() {})
                .join();

        var stat = webSocket.sendClose(WebSocket.NORMAL_CLOSURE,"");
        stat.join();
    }
}
