package com.cl3t4p.TinyNode.routes.api;

import com.cl3t4p.TinyNode.TinyNode;
import com.cl3t4p.TinyNode.config.ConfigFile;
import com.cl3t4p.TinyNode.tools.AESTools;
import io.javalin.apibuilder.ApiBuilder;
import io.javalin.http.Context;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.simple.SimpleLoggerFactory;

public class IPHandler {

  private static final Logger logger =
      new SimpleLoggerFactory().getLogger(IPHandler.class.getSimpleName());

  private static String IP = null;

  public IPHandler() {
    try (ScheduledExecutorService exec = Executors.newScheduledThreadPool(1)) {
      exec.scheduleAtFixedRate(new IPGrabber(), 0, 1, TimeUnit.MINUTES);
    }
  }

  public static void getEndpoints() {
    IPHandler ipHandler = new IPHandler();
    ApiBuilder.get("/", ipHandler::getCurrentIP);
  }

  /**
   * Retrieves the current IP address from the IP grabber URL and encrypts it using AES shared key.
   * If the IP address is not available, it returns a 401 Unauthorized status.
   */
  public void getCurrentIP(@NotNull Context ctx) {
    if (IP == null) {
      ctx.status(HttpStatus.UNAUTHORIZED_401);
    } else {
      ctx.result(AESTools.encryptFromByteToBase64(IP.getBytes(), TinyNode.getGlobalSecretKey()));
    }
  }

  /**
   * IPGrabber - A class that retrieves the current IP address from a specified URL. It implements
   * the Runnable interface to be used in a scheduled executor service.
   */
  public static class IPGrabber implements Runnable {

    private final URL ip_grabber_url;

    public IPGrabber() {
      ConfigFile configFile = new ConfigFile();
      try {
        ip_grabber_url = new URI(configFile.getIp_grabber()).toURL();
      } catch (MalformedURLException | URISyntaxException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void run() {
      try (var is = ip_grabber_url.openStream()) {
        IP = new String(is.readAllBytes());
      } catch (IOException e) {
        logger.error(e.getLocalizedMessage());
      }
    }
  }
}
