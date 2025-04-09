package com.cl3t4p.TinyNode.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ConfigFile {
    String ip_grabber;
    String shared_key;
    DatabaseConfig db;
}
