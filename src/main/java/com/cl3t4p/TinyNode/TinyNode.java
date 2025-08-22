package com.cl3t4p.TinyNode;

import com.cl3t4p.TinyNode.config.ConfigManager;
import com.cl3t4p.TinyNode.db.RepoManager;
import com.cl3t4p.TinyNode.model.BaseDevice;
import com.cl3t4p.TinyNode.routes.APIHandler;
import com.cl3t4p.TinyNode.routes.api.IPHandler;
import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Base64;
import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.simple.SimpleLoggerFactory;

public class TinyNode {

  private static final Logger logger =
      new SimpleLoggerFactory().getLogger(IPHandler.class.getSimpleName());

  private static final File CONFIG_FOLDER = new File("config");
  private static final File DATA_FOLDER = new File("data");

  @Getter private static byte[] globalSecretKey;

  @Getter private static ConfigManager cfgManager;

  @Getter private static Javalin app;

  public static void main(String[] args) {

    // Setup folders
    if (!CONFIG_FOLDER.exists()) {
      CONFIG_FOLDER.mkdirs();
    }
    if (!DATA_FOLDER.exists()) {
      DATA_FOLDER.mkdirs();
    }
    // Setup config

    try {
      File config_file = new File(CONFIG_FOLDER, "config.json");
      if (!config_file.exists()) {
        URL url = TinyNode.class.getClassLoader().getResource("config.json");
        Files.copy(url.openStream(), config_file.toPath());
      }
      cfgManager = ConfigManager.init(config_file);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // Setup shared aes key
    globalSecretKey = Base64.getDecoder().decode(cfgManager.getConfig().getShared_key());

    // Setup database
    try {
      RepoManager.init();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    app =
        Javalin.create(
            config -> {
              config.router.apiBuilder(
                  () -> {
                    ApiBuilder.path("/api", APIHandler.getEndpoints());
                    ApiBuilder.get("/info", ctx -> ctx.result("hello"));
                  });
              config.jetty.defaultHost = cfgManager.getConfig().getIp();
            });

    // TODO Remove after testing
    // app.error(500,ctx -> ctx.result(""));

    app.start(cfgManager.getConfig().getPort());
  }

  @SneakyThrows
  private static void add_device() {
    BaseDevice device = new BaseDevice();
    device.setId("80F3DA41139C");
    device.setName("Testing_Node");
    device.setPrivate_key(
        Base64.getDecoder().decode("HYeu2i6jCci64w92/OC2KpOX385MXzi4TxgXOkt+Xy0="));
    System.out.println(device.getPrivate_key().length);
    RepoManager.getInstance()
        .getDeviceRepo()
        .addDevice(device.getId(), device.getName(), device.getPrivate_key());
  }
}
