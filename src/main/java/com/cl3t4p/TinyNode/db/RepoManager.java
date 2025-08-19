package com.cl3t4p.TinyNode.db;

import com.cl3t4p.TinyNode.config.ConfigManager;
import com.cl3t4p.TinyNode.db.impl.DeviceRepoSQLite;
import com.cl3t4p.TinyNode.tools.MirroredSession;
import java.sql.SQLException;
import lombok.Getter;

/**
 * RepoManager - A singleton class to manage database repositories. It initializes the appropriate
 * repository based on the configuration.
 */
public class RepoManager {

  @Getter private static RepoManager instance;

  @Getter private final DeviceRepo deviceRepo;

  @Getter private final MirroredSession deviceSessionsMap;

  private RepoManager() throws SQLException {
    var config = ConfigManager.getInstance().getConfig().getDb();
    switch (config.db_type()) {
      case SQLite -> {
        deviceRepo = new DeviceRepoSQLite(config.url());
      }
      default -> throw new RuntimeException("Unknown database type");
    }

    deviceSessionsMap = new MirroredSession();
  }

  public static void init() throws SQLException {
    instance = new RepoManager();
  }

  public enum DatabaseType {
    SQLite
  }
}
