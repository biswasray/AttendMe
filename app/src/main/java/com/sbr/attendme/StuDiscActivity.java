package com.sbr.attendme;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
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
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StuDiscActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener ,Runnable{
    private WifiManager manager;
    private WifiP2pManager p2pmanager;
    private WifiP2pManager.Channel channel;
    private RippleBackground studentRipple;
    private ListView stuTeaList;
    private DiscListAdapter adapter;
    private ArrayList<String> arr;
    private ArrayList<String> arr1;
    private Student student;
    private SwipeRefreshLayout layout;

    private boolean isAnimationRunning=false;
    private Thread animation;
    private ImageView[] findTeach=new ImageView[3];
    private int[] ft={R.id.teach0,R.id.teach1,R.id.teach2};
    private static int index;
    private ArrayList<String> animList=new ArrayList<>();
    private float screenWidth,screenHeight;
    private ColorPicker colorPicker;

    final HashMap<String,Map> teacherMap=new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_disc);
        layout=(SwipeRefreshLayout)findViewById(R.id.swipeStu);
        layout.setOnRefreshListener(this);
        manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        p2pmanager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        studentRipple =(RippleBackground) findViewById(R.id.stu_ripple);
        colorPicker=new ColorPicker(this.getApplicationContext());
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenWidth = displaymetrics.widthPixels;
        screenHeight = displaymetrics.heightPixels;
        stuTeaList=(ListView)findViewById(R.id.stu_tea_list);
        arr=new ArrayList<>();
        arr1=new ArrayList<>();
        adapter=new DiscListAdapter(this, android.R.layout.simple_list_item_1,arr,arr1);
        stuTeaList.setAdapter(adapter);
        stuTeaList.post(new Runnable() {
            @Override
            public void run() {
                stuTeaList.smoothScrollToPosition(0);
            }
        });
        animation=new Thread(this);
        for(int j=0;j<3;j++)
            findTeach[j]=(ImageView)findViewById(ft[j]);
        channel = p2pmanager.initialize(this, getMainLooper(), null);
        check();
    }
    public void check() {
        if(manager.isWifiEnabled()) {
            proceed();
        }
        else {
            /*if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q) {
                Toast.makeText(this,"Please turn on WiFi",Toast.LENGTH_SHORT).show();
                Intent panel=new Intent(Settings.Panel.ACTION_WIFI);
                //startActivityForResult(panel,1);
                wifiOpener.launch(panel);
            }
            else {
                manager.setWifiEnabled(true);
            }*/
            manager.setWifiEnabled(true);
            if(manager.isWifiEnabled())
                proceed();
            else {
                Toast.makeText(this,"Please turn on WiFi",Toast.LENGTH_SHORT).show();
                wifiOpener.launch(new Intent(Settings.ACTION_WIFI_SETTINGS));
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
        Toast.makeText(this,"Searching for class",Toast.LENGTH_SHORT).show();
        startRegistration();
        discoverService();
        /*RippleBackground rp=(RippleBackground) findViewById(R.id.rippletest);
        ImageView imageView=(ImageView)findViewById(R.id.search_bud);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rp.isRippleAnimationRunning())
                    rp.stopRippleAnimation();
                else
                    rp.startRippleAnimation();
            }
        });*/
    }
    private void startRegistration() {
        student=MainActivity.db.getStudents().get(0);
        Map<String,String> record=new HashMap<>();
        record.put("listenport",Integer.toString(MainActivity.SERVER_PORT));
        record.put("appname",""+getResources().getString(R.string.app_name));
        record.put("type",""+getResources().getString(R.string.student));
        record.put("student",""+student.getName());
        record.put("student_id",student.getId());
        record.put("student_roll",""+student.getRoll());
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
        isAnimationRunning=true;
        animation.start();
        WifiP2pManager.DnsSdTxtRecordListener txtRecordListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(String s, Map<String, String> map, WifiP2pDevice wifiP2pDevice) {
                if(map.get("appname").equals(getResources().getString(R.string.app_name))&&map.get("listenport").equals(Integer.toString(MainActivity.SERVER_PORT))&&map.get("type").equals(getResources().getString(R.string.teacher))) {
                    teacherMap.put(wifiP2pDevice.deviceAddress,map);
                    //do something
                }
            }
        };
        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {

            @Override
            public void onDnsSdServiceAvailable(String s, String s1, WifiP2pDevice wifiP2pDevice) {
                if(teacherMap.containsKey(wifiP2pDevice.deviceAddress)) {
                    arr.add(0,(String) ((Map)teacherMap.get(wifiP2pDevice.deviceAddress)).get("class"));
                    arr1.add(0,(String) ((Map)teacherMap.get(wifiP2pDevice.deviceAddress)).get("teacher"));
                    animList.add((String) ((Map)teacherMap.get(wifiP2pDevice.deviceAddress)).get("class"));
                    //adapter.notifyDataSetChanged();
                }
            }
        };
        p2pmanager.setDnsSdResponseListeners(channel,servListener,txtRecordListener);
        ((ImageView)findViewById(R.id.search_bud1)).setImageDrawable(TextDrawable.builder()
                .beginConfig()
                .bold()
                .toUpperCase()
                .textColor(Color.WHITE)
                .endConfig().buildRound(""+student.getName().charAt(0), Color.BLACK));
        studentRipple.startRippleAnimation();
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
        isAnimationRunning=false;
        index=0;
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
        if(studentRipple.isRippleAnimationRunning())
            studentRipple.stopRippleAnimation();
    }
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            isAnimationRunning=false;
            index=0;
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                layout.setRefreshing(false);
            }
        }, 2000);
    }

    public void run() {
        while (isAnimationRunning) {
            if(index< animList.size()) {
                runOnUiThread(()-> {
                    fun();
                });
            }
            try {
                Thread.sleep(500);
            }
            catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }
    public void fun() {
        AnimatorSet appear=new AnimatorSet();
        ObjectAnimator scaleX=ObjectAnimator.ofFloat(findTeach[index%3],"ScaleX",0f,1.2f,1f);
        ObjectAnimator scaleY=ObjectAnimator.ofFloat(findTeach[index%3],"ScaleY",0f,1.2f,1f);
        appear.play(scaleX).with(scaleY);
        appear.setDuration(300);
        AnimatorSet move=new AnimatorSet();
        ObjectAnimator a1=ObjectAnimator.ofFloat(findTeach[index%3],"translationX",(screenWidth/2));
        ObjectAnimator a2=ObjectAnimator.ofFloat(findTeach[index%3],"translationY",screenHeight);
        move.play(a1).with(a2).after(500);
        move.setDuration(700);
        AnimatorSet total=new AnimatorSet();
        total.play(appear).before(move);
        findTeach[index%3].setImageDrawable(TextDrawable.builder()
                .beginConfig()
                .bold()
                .toUpperCase()
                .textColor(Color.WHITE)
                .endConfig().buildRound(""+animList.get(index).charAt(0), colorPicker.pickColor(animList.get(index))));
        findTeach[index%3].setVisibility(View.VISIBLE);
        total.start();
        total.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        index++;
    }
}