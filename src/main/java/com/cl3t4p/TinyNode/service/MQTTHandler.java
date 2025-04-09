package com.cl3t4p.TinyNode.service;


import com.cl3t4p.TinyNode.config.ConfigManager;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MQTTHandler {

    private final MqttClient client;
    public MQTTHandler() throws MqttException {
        var config = ConfigManager.getInstance().getConfig().getMqtt();

        client = new MqttClient(config.url(),config.client_id());

    }
}
