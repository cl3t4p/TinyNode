package com.cl3t4p.TinyNode.db;

import com.cl3t4p.TinyNode.devices.SimpleDevice;

import java.sql.SQLException;
import java.util.Set;

public interface DeviceRepo {

    boolean addDevice(String id,byte[] key) throws SQLException;

    boolean addDevice(String id,byte[] key,String name) throws SQLException;

    boolean modifyNameByID(String id,String name) throws SQLException;

    SimpleDevice getDeviceByID(String id) throws SQLException;

    Set<SimpleDevice> getAllDevices() throws SQLException;


}
