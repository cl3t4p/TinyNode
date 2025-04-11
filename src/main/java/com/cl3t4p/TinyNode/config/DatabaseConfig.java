package com.cl3t4p.TinyNode.config;

import com.cl3t4p.TinyNode.db.RepoManager;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatabaseConfig(RepoManager.DatabaseType db_type, String url) {}
