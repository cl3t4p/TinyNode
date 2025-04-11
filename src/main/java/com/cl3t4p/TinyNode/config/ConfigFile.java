package com.cl3t4p.TinyNode.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ConfigFile {
  Integer port;
  String ip_grabber;
  String shared_key;
  DatabaseConfig db;
}
