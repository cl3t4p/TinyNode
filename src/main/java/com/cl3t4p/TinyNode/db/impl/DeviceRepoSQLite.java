package com.cl3t4p.TinyNode.db.impl;

import com.cl3t4p.TinyNode.db.DeviceRepo;
import com.cl3t4p.TinyNode.db.mapper.SQLMapper;
import com.cl3t4p.TinyNode.model.BaseDevice;
import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeviceRepoSQLite implements DeviceRepo {

    private final String uri;
  private Connection conn = null;

    public DeviceRepoSQLite(String uri) throws SQLException {
        this.uri = uri;
    String table_creation =
        """
                CREATE TABLE IF NOT EXISTS `devices` (
                  `id` TEXT NOT NULL,
                  `name` TEXT NULL,
                  `private_key` BLOB NOT NULL CHECK (length(private_key) = 32),
                  PRIMARY KEY (`id`)
                )
                """;
        try (var conn = getConnection()) {
            var sta = conn.prepareStatement(table_creation);
            sta.executeUpdate();
        }
    }

    private Connection getConnection(){
        try {
            if(conn == null || conn.isClosed()){
                conn = DriverManager.getConnection(uri);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }

  @Override
  public BaseDevice getDeviceByID(String id) throws SQLException {
        try(var conn = getConnection()){
            var statement = conn.prepareStatement("SELECT * FROM `devices` WHERE id=?");
            statement.setString(1,id);
            var resultSet = statement.executeQuery();
      return SQLMapper.deserializeSQL(resultSet, BaseDevice.class);
        }
    }
    @Override
    public boolean addDevice(String id,byte[] key) throws SQLException{
        try(var conn = getConnection()){
      var statement =
          conn.prepareStatement("INSERT INTO `devices` (id,private_key,name) VALUES (?,?,?)");
            statement.setString(1,id);
            statement.setBlob(2,new ByteArrayInputStream(key));
            statement.setString(3,id);
            int result = statement.executeUpdate();
            return result == 1;
        }
    }

  @Override
  public boolean addDevice(BaseDevice device) throws SQLException {
        try(var conn = getConnection()){
      var statement =
          conn.prepareStatement("INSERT INTO `devices` (id,private_key,name) VALUES (?,?,?)");
      SQLMapper.serializeSQL(statement, device, List.of("id", "private_key", "name"));
            int result = statement.executeUpdate();
            return result == 1;
        }
    }

    @Override
    public boolean modifyNameByID(String id, String name) throws SQLException {
        try(var conn = getConnection()){
            var statement = conn.prepareStatement("UPDATE `devices` SET name=? WHERE id=?");
            statement.setString(1,name);
            statement.setString(2,id);
            int result = statement.executeUpdate();
            return result == 1;
        }
    }

  @Override
  public Set<BaseDevice> getAllDevices() throws SQLException {
    Set<BaseDevice> devices = new HashSet<>();
        try(var conn = getConnection()){
            var statement = conn.prepareStatement("SELECT * FROM `devices`");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
        BaseDevice device = SQLMapper.deserializeSQL(resultSet, BaseDevice.class);
                devices.add(device);
            }

        }
        return devices;
    }



}
