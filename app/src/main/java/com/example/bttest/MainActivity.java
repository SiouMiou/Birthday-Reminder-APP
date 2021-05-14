package com.example.bttest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
        private static final String TAG = MainActivity.class.getSimpleName()+"My";
        RecyclerView recyclerView;
        private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
        private static final int REQUEST_FINE_LOCATION_PERMISSION = 102;
        private boolean isScanning = false;
        ArrayList<ScannedData> findDevice = new ArrayList<>();
        MyAdapter mAdapter;

        BluetoothLeScanner mBluetoothLeScanner;
        BluetoothLeScanner mBmBluetoothAdapteroothLeScanner;
        BluetoothDevice testDeive ;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            BluetoothAdapter  mBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
            /**權限相關認證*/
            checkPermission(mBluetoothAdapter);
            /**初始藍牙掃描及掃描開關之相關功能*/
            bluetoothScan(mBluetoothAdapter);
            /**取得欲連線之裝置後跳轉頁面*/
            //mAdapter.OnItemClick(itemClick);

        }
        /**權限相關認證*/
        private void checkPermission(BluetoothAdapter  mBluetoothAdapter) {


                /**確認是否已開啟取得手機位置功能以及權限*/
                int hasGone = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
                if (hasGone != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            1);
                }

                /**確認手機是否支援藍牙BLE*/
                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    Toast.makeText(this,"Not support Bluetooth", Toast.LENGTH_SHORT).show();

                }
                /**開啟藍芽適配器*/
               /*if(!mBluetoothAdapter.isEnabled()){
                   Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                   startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                   Toast.makeText(this,"Bluetooth enabled", Toast.LENGTH_SHORT).show();
                   if (!mBluetoothAdapter.isEnabled()) {
                       Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                       startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                   }
            else{
                Toast.makeText(getApplicationContext(),"Bluetooth is already on", Toast.LENGTH_SHORT).show();
            }
                }*/

           /* try {
                if (mBluetoothAdapter != null &&!mBluetoothAdapter.isEnabled()) {

                    Toast.makeText(getApplicationContext(),"Please open Bluetooth", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Bluetooth is already on", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                }*/

        }
        /**初始藍牙掃描及掃描開關之相關功能*/
        private void bluetoothScan(BluetoothAdapter  mBluetoothAdapter) {
           // BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

            /**啟用藍牙適配器*/


            recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            // 設置格線
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


            /**開始掃描*/
            final Button btScan = findViewById(R.id.ButStartScan);
            btScan.setOnClickListener((v)-> {
                if(!mBluetoothAdapter.isEnabled()) {
                    Toast.makeText(getApplicationContext(), "Please enable BlueTooth", Toast.LENGTH_SHORT).show();
                }
                else{
                    if (!isScanning) {
                        //mBluetoothAdapter.startLeScan(mLeScanCallback);
                        if(mBluetoothAdapter.isEnabled()) {
                            isScanning = true;
                            btScan.setText("關閉掃描");
                            findDevice.clear(); // clear items
                            mBluetoothLeScanner.startScan(leScanCallback);
                            Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();
                            mAdapter = new MyAdapter(findDevice);
                            recyclerView.setAdapter(mAdapter);
                        }
                    }
                    else{
                        /**開啟掃描*/
                        isScanning = false;
                        btScan.setText("開啟掃描");
                        findDevice.clear();
                        mBluetoothLeScanner.stopScan(leScanCallback);
                        mAdapter.clearDevice();
                    }
                }


            });
            /**設置Recyclerview列表*/
            recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            // 設置格線
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        }
        @Override
        protected void onPause() {
            super.onPause();
            final Button btScan = findViewById(R.id.ButStartScan);

            isScanning = false;
            btScan.setText("開啟掃描");

            //mBluetoothAdapter.stopLeScan(mLeScanCallback);
            //mBluetoothLeScanner.startScan(startScanCallback);
            mBluetoothLeScanner.stopScan(leScanCallback);
        }

        /**顯示掃描到物件*/
        private ScanCallback leScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                BluetoothDevice device = result.getDevice();
                ScanRecord mScanRecord = result.getScanRecord();
                String address = device.getAddress();
                byte[] content = mScanRecord.getBytes();
                int mRssi = result.getRssi();

                findDevice.add(new ScannedData(device.getName()
                        , String.valueOf(mRssi)
                        , byteArrayToHexStr(content)
                        , device.getAddress()));
                ArrayList newList = getSingle(findDevice);
                mAdapter = new MyAdapter(newList);
                recyclerView.setAdapter(mAdapter);
            }
        };

        /**濾除重複的藍牙裝置(以Address判定)*/
        private ArrayList getSingle(ArrayList list) {
            ArrayList tempList = new ArrayList<>();
            try {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    Object obj = it.next();
                    if (!tempList.contains(obj)) {
                        tempList.add(obj);
                    }
                    else {
                        for (int i=0;i<tempList.size();i++)
                        {
                            if (tempList.get(i)==obj){
                                tempList.remove(i);
                                tempList.add(obj);
                            }

                        }

                    }
                }
                return tempList;
            } catch (ConcurrentModificationException e) {
                return tempList;
            }
        }
        /**
         * 以Address篩選陣列->抓出該值在陣列的哪處
         */
        private int getIndex(ArrayList temp, Object obj) {
            for (int i = 0; i < temp.size(); i++) {
                if (temp.get(i).toString().contains(obj.toString())) {
                    return i;
                }
            }
            return -1;
        }
        /**
         * Byte轉16進字串工具
         */
        static final char[] hexArray = "0123456789ABCDEF".toCharArray();
        public static String byteArrayToHexStr(byte[] bytes) {
            if (bytes == null) {
                return null;
            }

            char[] hexChars = new char[bytes.length * 2];
            for (int j = 0; j < bytes.length; j++) {
                int v = bytes[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }
            return new String(hexChars);
        }
}