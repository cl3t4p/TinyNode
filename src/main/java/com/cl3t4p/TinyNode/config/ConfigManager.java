package com.cl3t4p.TinyNode.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.File;
import java.io.IOException;


public class ConfigManager {

    @Getter
    private static ConfigManager instance;

    public static void init(File config_file) throws IOException {
        instance = new ConfigManager(config_file);
    }

    @Getter
    private final ConfigFile config;

    private ConfigManager(File config_file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        config = mapper.readValue(config_file,ConfigFile.class);

    }
}
