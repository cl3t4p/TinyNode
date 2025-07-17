package com.cl3t4p.TinyNode;

import com.cl3t4p.TinyNode.config.ConfigFile;
import com.cl3t4p.TinyNode.config.ConfigManager;
import com.cl3t4p.TinyNode.tools.AESTools;
import com.cl3t4p.TinyNode.tools.HexTools;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

public class WebSocketTest {

  private static final String API_BASE_PATH = "/api/v01";
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
    var bao = ByteBuffer.allocate(6 + Long.BYTES);
    bao.put(HexTools.decode(mac_address));

    String code =
        AESTools.encryptFromByteToBase64(
            bao.array(), Base64.getDecoder().decode(cfg.getShared_key()));

    WebSocket webSocket =
        client
            .newWebSocketBuilder()
            .header("Cookie", "code=" + code)
            .buildAsync(
                URI.create("ws://localhost:7070" + API_BASE_PATH + "/device/com"),
                new WebSocket.Listener() {})
            .join();


    var stat = webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "");
    stat.join();
  }
}
