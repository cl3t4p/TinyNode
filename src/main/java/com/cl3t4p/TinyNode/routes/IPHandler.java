package com.cl3t4p.TinyNode.routes;

import com.cl3t4p.TinyNode.config.ConfigFile;
import com.cl3t4p.TinyNode.config.ConfigManager;
import com.cl3t4p.TinyNode.tools.AESTools;
import io.javalin.http.Context;
import org.eclipse.jetty.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.simple.SimpleLoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class IPHandler {

    private static final Logger logger = new SimpleLoggerFactory().getLogger(IPHandler.class.getSimpleName());

    private static String IP = null;

    public IPHandler(){
        try(ScheduledExecutorService exec = Executors.newScheduledThreadPool(1)) {
            exec.scheduleAtFixedRate(new IPGrabber() , 0, 1, TimeUnit.MINUTES);
        }
    }


    /**
     * Get the current public ip of the machine encrypted with the shared aes key
     */
    public static void getCurrentIP(@NotNull Context ctx) {
        if (IP == null){
            ctx.status(HttpStatus.UNAUTHORIZED_401);
        }else{
            String key = ConfigManager.getInstance().getConfig().getKey();
            ctx.result(AESTools.encrypt(IP,key));
        }
    }



    public static class IPGrabber implements Runnable{

        private final URL ip_grabber_url;

        public IPGrabber(){
            ConfigFile configFile = new ConfigFile();
            try {
                ip_grabber_url = new URI(configFile.getIp_grabber()).toURL();
            } catch (MalformedURLException | URISyntaxException e) {
                throw new RuntimeException(e);
            }

        }


        @Override
        public void run() {
            try (var is = ip_grabber_url.openStream()){
                IP = new String(is.readAllBytes());
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage());
            }
        }
    }

}
