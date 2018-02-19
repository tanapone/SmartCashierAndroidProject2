package com.example.tanapone.smartcashier;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.widget.Toast;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by Tanapone on 13/2/2561.
 */

public class PrinterConnector extends Application {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;

    public void findBuletooth(){
        try{
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(bluetoothAdapter == null){
                Toast.makeText(getApplicationContext(),R.string.no_bluetooth_found,Toast.LENGTH_SHORT).show();
            }
            if(!bluetoothAdapter.isEnabled()){
                Toast.makeText(getApplicationContext(),R.string.enable_bluetooth_adapter,Toast.LENGTH_SHORT).show();
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
