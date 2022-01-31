package com.sbr.attendme;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class TeaDiscActivity extends AppCompatActivity {
    private WifiManager manager;
    private WifiP2pManager p2pmanager;
    private WifiP2pManager.Channel channel;
    private Classs classs;
    final HashMap<String, Map> studentMap = new HashMap<String, Map>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tea_disc);
        //get classs instances;
        manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        p2pmanager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = p2pmanager.initialize(this, getMainLooper(), null);
        check();
    }
    public void check() {
        if(manager.isWifiEnabled()) {
            proceed();
        }
        else {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q) {
                Toast.makeText(this,"Please turn on WiFi",Toast.LENGTH_SHORT).show();
                Intent panel=new Intent(Settings.Panel.ACTION_WIFI);
                //startActivityForResult(panel,1);
                wifiOpener.launch(panel);
            }
            else {
                manager.setWifiEnabled(true);
            }
        }
    }

    ActivityResultLauncher<Intent> wifiOpener= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 0) {
                        Intent data = result.getData();
                        check();
                    }
                }
            });

    public void proceed() {
        Toast.makeText(this,"Searching for students",Toast.LENGTH_SHORT).show();
        startRegistration();
        discoverService();
    }
    private void startRegistration() {
        Teacher teacher=MainActivity.db.getTeachers().get(0);
        Map record=new HashMap();
        record.put("listenport",Integer.toString(MainActivity.SERVER_PORT));
        record.put("appname",R.string.app_name);
        record.put("type",R.string.teacher);
        record.put("teacher",teacher.getName());
        record.put("class",classs.getSubject());
        WifiP2pDnsSdServiceInfo serviceInfo = WifiP2pDnsSdServiceInfo.newInstance("_test", "_presence._tcp", record);
        p2pmanager.addLocalService(channel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i) {

            }
        });
    }
    private void discoverService() {
        WifiP2pManager.DnsSdTxtRecordListener txtRecordListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(String s, Map<String, String> map, WifiP2pDevice wifiP2pDevice) {
                if(map.get("appname").equals(R.string.app_name)&&map.get("listenport").equals(Integer.toString(MainActivity.SERVER_PORT))&&map.get("type").equals(R.string.student)) {
                    studentMap.put(wifiP2pDevice.deviceAddress,map);
                }
            }
        };
        //To be continue....
    }
}