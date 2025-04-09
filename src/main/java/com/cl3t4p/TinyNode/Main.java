package com.cl3t4p.TinyNode;


import com.cl3t4p.TinyNode.config.ConfigManager;
import com.cl3t4p.TinyNode.routes.IPHandler;
import com.cl3t4p.TinyNode.routes.SimpleClientHandler;
import com.cl3t4p.TinyNode.db.RepoManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.simple.SimpleLoggerFactory;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;


public class Main {

    private static final Logger logger = new SimpleLoggerFactory().getLogger(IPHandler.class.getSimpleName());

    private static final File CONFIG_FOLDER = new File("config");
    private static final File DATA_FOLDER = new File("data");

    @Getter
    private static SecretKey globalSecretKey;

    @Getter
    private static ConfigManager cfgManager;

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
            ObjectMapper mapper = new ObjectMapper();
            cfgManager = mapper.readValue(new File(CONFIG_FOLDER,"config.json"),ConfigManager.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Setup databases
        RepoManager manager = RepoManager.getInstance();

        try {
            manager.init();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }




        var app = Javalin.create()
                .get("/",ctx -> ctx.result("Hello World"))
                .get("/api/devices/ip",IPHandler::getCurrentIP);

        app.start(7070);

    }



    private static void createFolder(){

    }

}