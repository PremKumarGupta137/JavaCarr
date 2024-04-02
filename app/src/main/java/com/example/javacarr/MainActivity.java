package com.example.javacarr;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_ENABLE_BLUETOOTH = 2;
    public static final String EXTRA_DEVICE_ADDRESS = "device_address";
    //These lines declare constants. REQUEST_ENABLE_BLUETOOTH is a request code used when enabling Bluetooth,
    // and EXTRA_DEVICE_ADDRESS is a key used to pass the device address between activities.

    public Button bon, boff, bnext, bshow;
    public TextView tname;

    public BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> devicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bon = findViewById(R.id.on);
        bshow = findViewById(R.id.show);
        bnext = findViewById(R.id.next);
        boff = findViewById(R.id.off);
        //  tname = findViewById(R.id.devicename);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        bon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableBluetooth();
            }
        });

        bshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPairedDevices();
            }
        });

        bnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedDeviceAddress = tname.getText().toString().trim();
                if (!selectedDeviceAddress.isEmpty()) {
                    connectToDevice(selectedDeviceAddress);
                } else {
                    Toast.makeText(MainActivity.this, "No device selected", Toast.LENGTH_SHORT).show();
                }
            }
        });


        boff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOffBluetooth();
            }
        });
    }

    public void enableBluetooth() {
        Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
    }

    public void showPairedDevices() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.isEmpty()) {
            tname.setText("No bonded devices");
        } else {
            ArrayList<String> deviceNames = new ArrayList<>();
            for (BluetoothDevice device : pairedDevices) {
                deviceNames.add(device.getName() + " - " + device.getAddress());
            }

            devicesArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceNames);
            //  This line creates an ArrayAdapter to adapt the deviceNames ArrayList to a ListView. android.R.layout.simple_list_item_1
            //  is a predefined layout provided by Android for a simple list item.

            ListView devicesListView = new ListView(this);
            devicesListView.setAdapter(devicesArrayAdapter);

            devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                    String selectedDevice = (String) adapterView.getItemAtPosition(position);
                    connectToDevice(selectedDevice);
                    // Set the selected device name in the TextView
                    tname.setText(selectedDevice);
                }
            });

            setContentView(devicesListView);
            //Finally, the layout of the activity is set to the ListView, displaying the list of paired devices to the user.
        }
    }
    public BluetoothSocket bluetoothSocket;
    public void connectToDevice(String selectedDevice) {
        String[] parts = selectedDevice.split(" - ");
        String deviceAddress = parts[1].trim(); // Extract the address

        // Get the Bluetooth device object using the address
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);

        try {
            // Create a Bluetooth socket using the UUID of the device
            bluetoothSocket = device.createRfcommSocketToServiceRecord(device.getUuids()[0].getUuid());
            bluetoothSocket.connect(); // Connect to the device
            moveToNextActivity(deviceAddress);
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "Failed to connect to device", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void moveToNextActivity(String deviceAddress) {
        Intent intent = new Intent(MainActivity.this, remote.class);
        // Pass the device address to the next activity
        intent.putExtra(EXTRA_DEVICE_ADDRESS, deviceAddress);
        startActivity(intent);
    }
    public void turnOffBluetooth() {
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
            tname.setText("");
            // Clear the device name TextView not required
            Toast.makeText(this, "Bluetooth turned off", Toast.LENGTH_SHORT).show();
        }
    }
}
