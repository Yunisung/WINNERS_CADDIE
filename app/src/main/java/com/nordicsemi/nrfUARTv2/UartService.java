
/*
 * Copyright (c) 2015, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.nordicsemi.nrfUARTv2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.widget.RemoteViews;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mtouch.ksnet.dpt.MainApplication;
import com.mtouch.ksnet.dpt.action.PayResultActivity;
import com.mtouch.ksnet.dpt.action.process.util.LOG;
import com.mtouch.ksnet.dpt.common.Utils;
import com.mtouch.ksnet.dpt.design.appToApp.network.ApiUtils;
import com.mtouch.ksnet.dpt.design.util.SharedPreferenceUtil;
import com.mtouch.ksnet.dpt.ks03.pay.Constants;
import com.mtouch.caddie.BuildConfig;
import com.mtouch.caddie.R;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class UartService extends Service {
//    private final static String TAG = UartService.class.getSimpleName();
    private final static String TAG = "UartService_BTJYP";

    public static final String KEY_ACTION = "key_action";
    public static final String ACTION_CONNECT = "actin_connect";
    public static final String ACTION_STOP = "actin_stop";

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.nordicsemi.nrfUART.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.nordicsemi.nrfUART.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.nordicsemi.nrfUART.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.nordicsemi.nrfUART.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.nordicsemi.nrfUART.EXTRA_DATA";
    public final static String DEVICE_DOES_NOT_SUPPORT_UART =
            "com.nordicsemi.nrfUART.DEVICE_DOES_NOT_SUPPORT_UART";
    
    public static final UUID TX_POWER_UUID = UUID.fromString("00001804-0000-1000-8000-00805f9b34fb");
    public static final UUID TX_POWER_LEVEL_UUID = UUID.fromString("00002a07-0000-1000-8000-00805f9b34fb");
    public static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID FIRMWARE_REVISON_UUID = UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb");
    public static final UUID DIS_UUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
//    public static final UUID RX_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
//    public static final UUID RX_CHAR_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
//    public static final UUID TX_CHAR_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");

    // UUIDs for UART service and associated characteristics.
    public static UUID RX_SERVICE_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    public static UUID RX_CHAR_UUID   = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    public static UUID TX_CHAR_UUID   = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");

    private String getStatus(int status){
        if(status == BluetoothProfile.STATE_DISCONNECTED){
            return "STATE_DISCONNECTED";
        }else if(status == BluetoothProfile.STATE_CONNECTING){
            return "STATE_CONNECTING";
        }else if(status == BluetoothProfile.STATE_CONNECTED){
            return "STATE_CONNECTED";
        }else if(status == BluetoothProfile.STATE_DISCONNECTING){
            return "STATE_DISCONNECTING";
        }else {
            return status+"";
        }
    }
    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if(PayResultActivity.TEST) LOG.e(TAG,"status: "+getStatus(status)+" newState: "+ getStatus(newState));


            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                ((MainApplication)getApplication()).setIsBlueToothConnect(true);
                broadcastUpdate(intentAction);
                if(PayResultActivity.TEST) LOG.w(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connect1ion.

                if(PayResultActivity.TEST) LOG.w(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());


                changeStateNotification(true);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                ((MainApplication)getApplication()).setIsBlueToothConnect(false);
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                if(PayResultActivity.TEST) LOG.e(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);

                close();
                changeStateNotification(false);
            }



        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
            	if(PayResultActivity.TEST) LOG.w(TAG, "mBluetoothGatt = " + mBluetoothGatt );
            	
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                if(PayResultActivity.TEST) LOG.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
            if(PayResultActivity.TEST) LOG.w(TAG,"status: "+status+" characteristic: uuid : "+characteristic.getUuid()+" value: "+characteristic.getValue() );
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            if(PayResultActivity.TEST) LOG.w(TAG," characteristic:  uuid: "+characteristic.getUuid()+" value: "+characteristic.getValue().toString() );
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    /**
     * notification bluetooth connect state
     * @param isConnected
     */
    private void changeStateNotification(boolean isConnected) {
        if(!SharedPreferenceUtil.getData(this, Constants.KEY_KEEP_CONNECTION,"false").equals("true")){
            return;
        }

        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.notification_foreground);
        if(isConnected){
            remoteViews.setTextViewText(R.id.messageTextView, "블루투스가 연결되었습니다.");
            remoteViews.setTextColor(R.id.messageTextView, ContextCompat.getColor(UartService.this, R.color.algae_green));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);

                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

                notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContent(remoteViews)
                        .setSmallIcon(R.drawable.ic_mtouch_logo_white)
                        .build();

                startForeground(2, notification);
            }else{
                notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContent(remoteViews)
                        .setSmallIcon(R.drawable.ic_mtouch_logo_white)
                        .build();

                startForeground(2, notification);
            }
        }else{
            remoteViews.setTextViewText(R.id.messageTextView, "블루투스가 연결되지 않았습니다.(터치시 연결시도)");
            remoteViews.setTextColor(R.id.messageTextView, ContextCompat.getColor(UartService.this, R.color.watermelon));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);

                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

                Intent notificationIntent = new Intent(this, UartService.class);
                notificationIntent.putExtra(KEY_ACTION,ACTION_CONNECT);
                PendingIntent pendingIntent = PendingIntent.getForegroundService(this, 0, notificationIntent, 0);


                notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContent(remoteViews)
                        .setSmallIcon(R.drawable.ic_mtouch_logo_white)
                        .setContentIntent(pendingIntent)
                        .build();

                startForeground(2, notification);
            }else{

                Intent notificationIntent = new Intent(this, UartService.class);
                notificationIntent.putExtra(KEY_ACTION,ACTION_CONNECT);
                PendingIntent pendingIntent = PendingIntent.getService(this, 0, notificationIntent, 0);


                notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContent(remoteViews)
                        .setSmallIcon(R.drawable.ic_mtouch_logo_white)
                        .setContentIntent(pendingIntent)
                        .build();

                startForeground(2, notification);
            }
        }

    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is handling for the notification on TX Character of NUS service
        if (TX_CHAR_UUID.equals(characteristic.getUuid())) {
        	
           // if(PayResultActivity.TEST) LOG.d(TAG, String.format("Received TX: %d",characteristic.getValue() ));
            intent.putExtra(EXTRA_DATA, characteristic.getValue());
        } else {
        	
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    private Notification notification;
    private String CHANNEL_ID = "mtouch_ksr03_bluetooth_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        if(PayResultActivity.TEST) LOG.d("====================== onCreate() ======================");
        if(SharedPreferenceUtil.getData(this, Constants.KEY_KEEP_CONNECTION,"false").equals("true")) {
            changeStateNotification(false);
            initialize();
            connect(SharedPreferenceUtil.getData(this, Constants.KEY_MAC_ADDRESS, null));
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(PayResultActivity.TEST) LOG.d("====================== onStartCommand() ======================");

        if(intent!=null){
            String action = intent.getStringExtra(KEY_ACTION);
            if(action!=null) {
                if (action.equals(ACTION_CONNECT)) {
                    initialize();
                    connect(SharedPreferenceUtil.getData(this, Constants.KEY_MAC_ADDRESS, null));
                } else if (action.equals(ACTION_STOP)) {
                    close();
                    stopForeground(true);
                    stopSelf();
                }
            }
        }

        return START_STICKY;
    }



    public class LocalBinder extends Binder {
        public UartService getService() {
            return UartService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        if(!SharedPreferenceUtil.getData(this,Constants.KEY_KEEP_CONNECTION,"false").equals("true")) {
            close();
        }
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                if(PayResultActivity.TEST) LOG.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            if(PayResultActivity.TEST) LOG.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            if(PayResultActivity.TEST) LOG.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress == null || !address.equals(mBluetoothDeviceAddress) || mBluetoothGatt == null) {
            BluetoothDevice remoteDevice = this.mBluetoothAdapter.getRemoteDevice(address);
            if (remoteDevice == null) {
                if(PayResultActivity.TEST) LOG.w(TAG, "###***Device not found.  Unable to connect.");
                return false;
            }
            this.mBluetoothGatt = remoteDevice.connectGatt(this, false, this.mGattCallback);
            if(PayResultActivity.TEST) LOG.d(TAG, "###***Trying to create a new connection.");
            this.mBluetoothDeviceAddress = address;
            this.mConnectionState = STATE_CONNECTING;
            return true;
        }

        if(PayResultActivity.TEST) LOG.d(TAG, "###***Trying to use an existing mBluetoothGatt for connection.");
        if (!this.mBluetoothGatt.connect()) {
            return false;
        }
        this.mConnectionState = STATE_CONNECTING;
        return true;

    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            if(PayResultActivity.TEST) LOG.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
       // mBluetoothGatt.close();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
//        ((MainApplication)getApplication()).setIsBlueToothConnect(false);
        if (mBluetoothGatt != null) {
            try {
                if(PayResultActivity.TEST) LOG.w(TAG, "mBluetoothGatt closed");
                mBluetoothDeviceAddress = null;
                mBluetoothGatt.close();
                mBluetoothGatt = null;
            }catch (Exception e){e.printStackTrace();}
        }
    }
    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            if(PayResultActivity.TEST) LOG.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.*
    */
    
    /**
     * Enable Notification on TX characteristic
     *
     * @return 
     */
    public void enableTXNotification()
    { 
    	/*
    	if (mBluetoothGatt == null) {
    		showMessage("mBluetoothGatt null" + mBluetoothGatt);
    		broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
    		return;
    	}
    		*/
    	BluetoothGattService RxService = mBluetoothGatt.getService(RX_SERVICE_UUID);
    	if (RxService == null) {
            showMessage("Rx service not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return;
        }
    	BluetoothGattCharacteristic TxChar = RxService.getCharacteristic(TX_CHAR_UUID);
        if (TxChar == null) {
            showMessage("Tx charateristic not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(TxChar,true);
        
        BluetoothGattDescriptor descriptor = TxChar.getDescriptor(CCCD);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);

    }
    
    public void writeRXCharacteristic(byte[] value) {
        if (mBluetoothGatt == null) {
            if(PayResultActivity.TEST) LOG.d(TAG, "###***mBluetoothGatt == null");
            return;
        }

        BluetoothGattService RxService = mBluetoothGatt.getService(RX_SERVICE_UUID);
        showMessage("###***mBluetoothGatt " + this.mBluetoothGatt);

    	if (RxService == null) {
             showMessage("Rx service not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return;
        }
    	BluetoothGattCharacteristic RxChar = RxService.getCharacteristic(RX_CHAR_UUID);
        if (RxChar == null) {
            showMessage("Rx charateristic not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return;
        }

        RxChar.setValue(value);
    	boolean status = mBluetoothGatt.writeCharacteristic(RxChar);

        if(PayResultActivity.TEST) LOG.d(TAG, "write TXchar - status=" + status+" value: "+value);
        if(BuildConfig.IS_DEVEL) {
            if(PayResultActivity.TEST) LOG.d(TAG,"\n\n::::::::::::[hex]" + Utils.byteArrayToHexString(value) + "\n[idx]" + value.length);
        }
    }

    private void showMessage(String msg) {
        if(PayResultActivity.TEST) LOG.e(TAG, msg);
    }
    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }
}
