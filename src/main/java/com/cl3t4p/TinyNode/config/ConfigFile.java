package com.cl3t4p.TinyNode.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ConfigFile {
    String ip_grabber;
    String key;
    DatabaseConfig db;
    MQTTConfig mqtt;
}
