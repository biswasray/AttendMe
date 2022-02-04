package com.sbr.attendme;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.skyfishjy.library.RippleBackground;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TeaDiscActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private WifiManager manager;
    private WifiP2pManager p2pmanager;
    private WifiP2pManager.Channel channel;
    private RippleBackground teacherRipple;
    private ListView teaStuList;
    private DiscListAdapter adapter;
    private ArrayList<String> arr;
    private ArrayList<String> arr1;
    private Classs classs;
    private SwipeRefreshLayout layout;
    final HashMap<String, Map> studentMap = new HashMap<String, Map>();
    private String dateData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tea_disc);
        layout=(SwipeRefreshLayout)findViewById(R.id.swipeTea);
        layout.setOnRefreshListener(this);
        classs= (Classs) getIntent().getSerializableExtra("classs");
        manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        p2pmanager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        teacherRipple=(RippleBackground)findViewById(R.id.tea_ripple);
        teaStuList=(ListView)findViewById(R.id.tea_stu_list);
        arr=new ArrayList<>();
        arr1=new ArrayList<>();
        adapter=new DiscListAdapter(this, android.R.layout.simple_list_item_1,arr,arr1);
        teaStuList.setAdapter(adapter);
        teaStuList.post(new Runnable() {
            @Override
            public void run() {
                teaStuList.smoothScrollToPosition(0);
            }
        });
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
        dateData= DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        if(MainActivity.db.tableExists(classs.getDateTable()))
        startRegistration();
        discoverService();
    }
    private void startRegistration() {
        Teacher teacher=MainActivity.db.getTeachers().get(0);
        Map<String,String> record=new HashMap();
        record.put("listenport",Integer.toString(MainActivity.SERVER_PORT));
        record.put("appname",""+getResources().getString(R.string.app_name));
        record.put("type",""+getResources().getString(R.string.teacher));
        record.put("teacher",teacher.getName());
        record.put("time",dateData);
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
        WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        p2pmanager.addServiceRequest(channel,serviceRequest,new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //Toast.makeText(getApplicationContext(),"add service req ",Toast.LENGTH_SHORT).show();
            }
            @Override public void onFailure(int code) { }
        });
    }
    private void discoverService() {
        WifiP2pManager.DnsSdTxtRecordListener txtRecordListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(String s, Map<String, String> map, WifiP2pDevice wifiP2pDevice) {
                if(map.get("appname").equals(getResources().getString(R.string.app_name))&&map.get("listenport").equals(Integer.toString(MainActivity.SERVER_PORT))&&map.get("type").equals(getResources().getString(R.string.student))) {
                    if(!studentMap.containsKey(wifiP2pDevice.deviceAddress)) {
                        studentMap.put(wifiP2pDevice.deviceAddress,map);

                    }
                }
            }
        };
        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {

            @Override
            public void onDnsSdServiceAvailable(String s, String s1, WifiP2pDevice wifiP2pDevice) {
                if(studentMap.containsKey(wifiP2pDevice.deviceAddress)) {
                    arr.add(0,(String) ((Map)studentMap.get(wifiP2pDevice.deviceAddress)).get("student"));
                    arr1.add(0,(String) ((Map)studentMap.get(wifiP2pDevice.deviceAddress)).get("student_roll"));
                    adapter.notifyDataSetChanged();
                }
            }
        };
        p2pmanager.setDnsSdResponseListeners(channel,servListener,txtRecordListener);
        ((ImageView)findViewById(R.id.search_bud0)).setImageDrawable(TextDrawable.builder()
                .beginConfig()
                .bold()
                .toUpperCase()
                .textColor(Color.WHITE)
                .endConfig().buildRound(""+classs.getSubject().charAt(0), Color.BLACK));
        teacherRipple.startRippleAnimation();
        p2pmanager.discoverServices(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        p2pmanager.clearLocalServices(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i) {

            }
        });
        p2pmanager.clearServiceRequests(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i) {

            }
        });
        p2pmanager.stopPeerDiscovery(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i) {

            }
        });
        if(teacherRipple.isRippleAnimationRunning())
            teacherRipple.stopRippleAnimation();
    }
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public void onRefresh() {
        this.onPause();
        this.onResume();
    }
}