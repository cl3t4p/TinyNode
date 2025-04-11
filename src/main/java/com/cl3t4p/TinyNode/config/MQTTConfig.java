package com.cl3t4p.TinyNode.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MQTTConfig(String client_id, String url) {}
