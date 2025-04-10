package com.cl3t4p.TinyNode;



import com.cl3t4p.TinyNode.config.ConfigManager;
import com.cl3t4p.TinyNode.routes.APIHandler;
import com.cl3t4p.TinyNode.routes.api.IPHandler;
import com.cl3t4p.TinyNode.db.RepoManager;


import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.simple.SimpleLoggerFactory;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;


public class TinyNode {

    private static final Logger logger = new SimpleLoggerFactory().getLogger(IPHandler.class.getSimpleName());

    private static final File CONFIG_FOLDER = new File("config");
    private static final File DATA_FOLDER = new File("data");

    @Getter
    private static SecretKey globalSecretKey;

    @Getter
    private static ConfigManager cfgManager;

    @Getter
    private static Javalin app;

    public static void main(String[] args) {

        //Setup folders
        if (!CONFIG_FOLDER.exists()){
            CONFIG_FOLDER.mkdirs();
        }
        if (!DATA_FOLDER.exists()){
            DATA_FOLDER.mkdirs();
        }
        //Setup config
        try {
            cfgManager = ConfigManager.init(new File(CONFIG_FOLDER,"config.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Setup shared aes key
        byte[] encodedKey = Base64.getDecoder().decode(cfgManager.getConfig().getShared_key());
        globalSecretKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");


        //Setup database
        try {
            RepoManager.init();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }

        app = Javalin.create(config->{
            config.router.apiBuilder(()->{
                ApiBuilder.path("/api",APIHandler.getEndpoints());
                ApiBuilder.get("/info", ctx -> ctx.result("hello"));
            });
        });

        app.start(cfgManager.getConfig().getPort());
    }




}