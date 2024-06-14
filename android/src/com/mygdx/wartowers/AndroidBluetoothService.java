package com.mygdx.wartowers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.mygdx.wartowers.Bluetooth.BluetoothServiceInterface;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class AndroidBluetoothService implements BluetoothServiceInterface {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-e8e0-00805F9B34FB");

    private static AndroidBluetoothService instance;
    private final AndroidLauncher androidLauncher;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 2;

    public static final int REQUEST_DISCOVERABLE_BT = 1;

    private final BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;
    private final Context context;

    private final ArrayList<String> messages = new ArrayList<>();

    public AndroidBluetoothService(AndroidLauncher androidLauncher, Context context) {
        this.androidLauncher = androidLauncher;
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public AndroidBluetoothService getInstance(Context context) {
        if (instance == null) {
            instance = new AndroidBluetoothService(androidLauncher, context);
        }
        return instance;
    }

    public static AndroidBluetoothService getInstance() {
        return instance;
    }

    @Override
    public void connectToAllBondedDevices() {
        if (!hasBluetoothPermissions()) {
            return;
        }

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        for (BluetoothDevice device : bondedDevices) {
            BluetoothSocket socket = null;
            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                bluetoothAdapter.cancelDiscovery();
                socket.connect();
                this.socket = socket;
            } catch (IOException e) {
                if (socket != null) {
                    closeSocketQuietly(socket);
                }
            }
        }
    }

    @Override
    public void connectToDeviceByName(String name) {
        for (BluetoothDevice device : getConnectedDevices()) {
            if (device.getName().equals(name)) {
                connectToDevice(device);
                break;
            }
        }
    }

    public boolean connectToDevice(BluetoothDevice device) {
        BluetoothSocket socket = null;
        try {
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            bluetoothAdapter.cancelDiscovery();
            socket.connect();
            if (socket.isConnected()) {
                Gdx.app.log("Bluetooth", "Connected to " + device.getName());
            }else{
                Gdx.app.log("Bluetooth", "Failed to connect to " + device.getName());
            }
            this.socket = socket;
            return true;
        } catch (IOException e) {
            Gdx.app.log("Bluetooth", "oooooooooops");
            if (socket != null) {
                closeSocketQuietly(socket);
            }
            return false;
        }
    }

    private boolean hasBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
    }

    @Override
    public void sendMessage(String message) {
        if (socket != null && socket.isConnected()) {
            try {
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(message.getBytes());
                outputStream.flush();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public void closeAllConnections() {
        closeSocketQuietly(socket);
        socket = null;
    }

    @Override
    public Array<String> getLastMessages() {
        Array<String> result;
        synchronized (messages) {
            result = new Array<>(messages.toArray(new String[0]));
            messages.clear();
        }
        return result;
    }

    private void closeSocketQuietly(BluetoothSocket socket) {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }

    public void listenForMessages(BluetoothSocket socket) {
        Thread messageThread = new Thread(() -> {
            try {
                InputStream inputStream = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int bytes;
                while ((bytes = inputStream.read(buffer)) != -1) {
                    String receivedMessage = new String(buffer, 0, bytes);
                    synchronized (messages) {
                        messages.add(receivedMessage);
                    }
                }
            } catch (IOException ignored) {
            }
        });
        messageThread.start();
    }

    @Override
    public void acceptConnections() {
        BluetoothServerSocket serverSocket = null;
        try {
            serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("MyService", MY_UUID);
            while (true) {
                BluetoothSocket socket = serverSocket.accept();
                if (socket != null) {
                    listenForMessages(socket);
                    serverSocket.close();
                    break;
                }
            }
        } catch (IOException ignored) {
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    @Override
    public boolean checkSocketConnection() {
        if (socket != null && !socket.isConnected()) {
            socket = null;
        }
        return socket != null;
    }

    @Override
    public UUID getMyUuid() {
        return MY_UUID;
    }

    @Override
    public void bluetoothConnectionProlong(){
        androidLauncher.bluetoothConnectionProlong();
    }

    public Set<BluetoothDevice> getConnectedDevices() {
        return bluetoothAdapter.getBondedDevices();
    }

    @Override
    public Array<String> getConnectedDevicesChoices() {
        Array<String> result = new Array<>();
        for(BluetoothDevice device : getConnectedDevices()) {
            result.add(device.getName());
        }
        return result;
    }


}

