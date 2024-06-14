package com.mygdx.wartowers;

import static com.mygdx.wartowers.AndroidBluetoothService.REQUEST_DISCOVERABLE_BT;
import static com.mygdx.wartowers.utils.Constants.REQUEST_BLUETOOTH_PERMISSIONS;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.util.Arrays;

public class AndroidLauncher extends AndroidApplication {
	private static final int REQUEST_ENABLE_BT = 1;
	private AndroidBluetoothService bluetoothService;
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
		config.useCompass = false;
		config.useGyroscope = true;

		Context context = getApplicationContext();
		bluetoothService = new AndroidBluetoothService(this, context);

		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (bluetoothAdapter == null) {
			return;
		}

		if (!bluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_DISCOVERABLE_BT);
		}

		new Thread(() -> bluetoothService.acceptConnections()).start();

		initialize(new WarTowers(new AndroidDBInterfaceClass(), bluetoothService), config);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode >= REQUEST_ENABLE_BT) {
			if (resultCode >= 0) {
				checkPermissionsAndConnect();
			} else {
				Toast.makeText(this, "Bluetooth must be enabled to proceed.", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
			System.out.println(Arrays.toString(permissions));
			if (grantResults.length == 0) {
				return;
			}
			if (!allPermissionsGranted(grantResults)) {
				Toast.makeText(this, "Bluetooth permissions are required to proceed.", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	private boolean allPermissionsGranted(int[] grantResults) {
		for (int result : grantResults) {
			if (result != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}

	private void checkPermissionsAndConnect() {
		if (!hasBluetoothPermissions()){
			requestBluetoothPermissions();
		}
	}

	private void requestBluetoothPermissions() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			ActivityCompat.requestPermissions(
					this,
					new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN},
					REQUEST_BLUETOOTH_PERMISSIONS
			);
		} else {
			ActivityCompat.requestPermissions(
					this,
					new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION},
					REQUEST_BLUETOOTH_PERMISSIONS
			);
		}
	}

	private boolean hasBluetoothPermissions() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
					&& ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
		} else {
			return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
					&& ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED
					&& ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
		}
	}

	public void bluetoothConnectionProlong(){
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 2000);
		startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE_BT);
		new Thread(() -> bluetoothService.acceptConnections()).start();
	}

}
