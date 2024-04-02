package com.example.javacarr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class remote extends AppCompatActivity {
    public ImageButton bForward, bBack, bLeft, bRight;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothDevice arduinoDevice;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private boolean isForwardPressed = false;
    public boolean isBackPressed = false;
    public boolean isLeftPressed = false;
    public boolean isRightPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote);

        bForward = findViewById(R.id.Forward);
        bBack = findViewById(R.id.Back);
        bLeft = findViewById(R.id.Left);
        bRight = findViewById(R.id.Right);

        connectToArduino();

        bForward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    isForwardPressed = true;
                    sendCommand("F");
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    isForwardPressed = false;
                    sendCommand("S");
                }
                return true;
            }
        });

        bBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    isForwardPressed = true;
                    sendCommand("B");
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    isForwardPressed = false;
                    sendCommand("S");
                }
                return true;
            }
        });

        bRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    isForwardPressed = true;
                    sendCommand("L");
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    isForwardPressed = false;
                    sendCommand("S");
                }
                return true;
            }
        });

        bLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    isForwardPressed = true;
                    sendCommand("R");
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    isForwardPressed = false;
                    sendCommand("S");
                }
                return true;
            }
        });
    }

    private void connectToArduino() {
        //String address = "00:23:02:35:12:09"; // Replace with your Arduino Bluetooth module MAC address
        String address = "00:23:09:01:61:8A"; // Replace with your Arduino Bluetooth module MAC address

        arduinoDevice = bluetoothAdapter.getRemoteDevice(address);
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // Standard UUID for SPP (Serial Port Profile)

        try {
            socket = arduinoDevice.createRfcommSocketToServiceRecord(uuid);
            socket.connect();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            // Display a toast message for the connection error
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(remote.this, "Failed to connect to Arduino device", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendCommand(String command) {
        //If the outputStream is not null, this block of code attempts to send the command to the Arduino. It converts the command
        // string into bytes using command.getBytes() and writes these bytes to the outputStream. If any IOException occurs during
        // this process, it will be caught, and the stack trace will be printed using e.printStackTrace().
        if (outputStream != null) {
            try {
                outputStream.write(command.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Output stream is null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}