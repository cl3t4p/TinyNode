package com.cl3t4p.TinyNode.db;

import com.cl3t4p.TinyNode.devices.SimpleDevice;

import java.sql.SQLException;
import java.util.Set;


/**
 * DeviceRepo - An interface for managing device repositories.
 * It provides methods to add, modify, and retrieve devices from the database.
 */
public interface DeviceRepo {


    /**
     * Adds a device to the database.
     *
     * @param id  The ID of the device.
     * @param key The AES key of the device.
     * @return true if the device was added successfully, false otherwise.
     * The device name is set to its ID by default.
     * @throws SQLException If an SQL error occurs during the operation.
     */
    boolean addDevice(String id,byte[] key) throws SQLException;

    /**
     * Adds a device to the database.
     *
     * @param device The SimpleDevice object to add.
     * @return true if the device was added successfully, false otherwise.
     * @throws SQLException If an SQL error occurs during the operation.
     */
    boolean addDevice(SimpleDevice device) throws SQLException;

    /**
     * Modifies the name of a device in the database.
     *
     * @param id   The ID of the device.
     * @param name The new name of the device.
     * @return true if the name was modified successfully, false otherwise.
     * @throws SQLException If an SQL error occurs during the operation.
     */
    boolean modifyNameByID(String id,String name) throws SQLException;


    /**
     * Retrieves a device from the database by its ID.
     * 
     * @param id The ID of the device.
     * @return The SimpleDevice object if found, null otherwise.
     * @throws SQLException If an SQL error occurs during the operation.
     */
    SimpleDevice getDeviceByID(String id) throws SQLException;


    /**
     * Retrieves all devices from the database.
     *
     * @return A set of SimpleDevice objects representing all devices in the database.
     * @throws SQLException If an SQL error occurs during the operation.
     */
    Set<SimpleDevice> getAllDevices() throws SQLException;

}
