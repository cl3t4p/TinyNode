package com.cl3t4p.TinyNode.db;

import com.cl3t4p.TinyNode.config.ConfigManager;
import com.cl3t4p.TinyNode.db.impl.DeviceRepoSQLite;
import lombok.Getter;


import java.sql.SQLException;



public class RepoManager {

    @Getter
    private static RepoManager instance;


    @Getter
    private final DeviceRepo deviceRepo;


    public void init() throws SQLException {
        instance = new RepoManager();
    }

    private RepoManager() throws SQLException {
        var config = ConfigManager.getInstance().getConfig().getDb();
        switch (config.db_type()){
            case SQLite -> {
                deviceRepo = new DeviceRepoSQLite(config.url());
            }
            default -> throw new RuntimeException("Unknown database type");
        }
    }


    public static enum DatabaseType{
        SQLite
    }


}
