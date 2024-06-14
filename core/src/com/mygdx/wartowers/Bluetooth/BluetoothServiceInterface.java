package com.mygdx.wartowers.Bluetooth;

import com.badlogic.gdx.utils.Array;

import java.util.UUID;

public interface BluetoothServiceInterface {

    void connectToAllBondedDevices();

    public void connectToDeviceByName(String name);

    void sendMessage(String message);

    void closeAllConnections();

    Array<String> getLastMessages();

    boolean checkSocketConnection();

    void acceptConnections();

    UUID getMyUuid();

    void bluetoothConnectionProlong();

    Array<String> getConnectedDevicesChoices();
}

/*
public interface BluetoothService {
    void startAcceptingConnections();
    void connectToDevice(String deviceAddress);
    void write(byte[] data);
    void setOnDataReceivedListener(OnDataReceivedListener listener);
    void  startDeviceDiscovery();

    interface OnDataReceivedListener {
        void onDataReceived(byte[] data);
    }
}*/
