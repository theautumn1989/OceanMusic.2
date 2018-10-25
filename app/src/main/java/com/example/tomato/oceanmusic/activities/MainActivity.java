package com.example.tomato.oceanmusic.activities;


import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.ServiceConnection;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.example.tomato.oceanmusic.R;
import com.example.tomato.oceanmusic.adapter.ViewPagerMainAdapter;
import com.example.tomato.oceanmusic.fragments.FragmentPlayingBar;
import com.example.tomato.oceanmusic.models.Song;
import com.example.tomato.oceanmusic.services.MusicService;
import com.example.tomato.oceanmusic.utils.DataCenter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final int VP_PAGE_LIMIT = 3;
    public static final int PERMISSION_KEY = 1989;

    FragmentPlayingBar fmPlayingBar;
    MusicService mService;
    int mPosition = -1;
    ArrayList<Song> arrSong;
    ViewPager vpMain;
    TabLayout tabMain;

    boolean mIsBound = false;

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MyBinder binder = (MusicService.MyBinder) service;
            mService = binder.getService();
            DataCenter.instance.musicService = mService;
            //   mService = (MusicService) DataCenter.instance.musicService;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("ServiceMusic", "init Activity");
        initPermission();
//        DataCenter.setStatusBarTranslucent(true, this);

        Log.d("123", "onCreate: ");
    }

    public void initData() {
        DataCenter.instance.mainActivity = this;
        mService = new MusicService();
        mService = (MusicService) DataCenter.instance.musicService;

        if (mService != null) {
            Log.d("ServiceMusic", "init");
            mPosition = mService.getPosition();
        } else {
            Log.d("ServiceMusic", "init");
            initService();
        }
    }

    public void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //Register permission
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_KEY);

            } else {

                initToolbar();
                initData();
                initView();
                init();
            }
        } else {
            initToolbar();
            initData();
            initView();
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_KEY: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initToolbar();
                    initData();
                    initView();
                    init();
                } else {
                    //  Toast.makeText(this, ""+R.string.notification_permissions, Toast.LENGTH_LONG).show();
                    Toast.makeText(this, "người dùng không cấp quyền cho ứng dụng", Toast.LENGTH_LONG).show();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                return;
            }
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.abc_ic_menu_moreoverflow_mtrl_alpha));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }


    private void init() {
        ViewPagerMainAdapter adapter = new ViewPagerMainAdapter(getSupportFragmentManager(), this);
        arrSong = new ArrayList<>();
        arrSong = DataCenter.instance.getListSong();
        vpMain.setAdapter(adapter);
        tabMain.setupWithViewPager(vpMain);
        vpMain.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabMain));
        vpMain.setOffscreenPageLimit(VP_PAGE_LIMIT);
        tabMain.setTabsFromPagerAdapter(adapter);
    }

    private void initView() {
        fmPlayingBar = (FragmentPlayingBar) getFragmentManager().findFragmentById(R.id.fm_playing_bar);
        vpMain = findViewById(R.id.viewpager_main);
        tabMain = findViewById(R.id.tablayout_main);
    }

    public void initService() {
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        startService(intent);
        mIsBound = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("123", "onDestroy: ");

        mService = (MusicService) DataCenter.instance.musicService;

        if (mIsBound) {
            unbindService(connection);
            mIsBound = false;
        }
        if (mService.isPlaying()) {
            DataCenter.instance.mainActivity = null;
        } else {
            mService.stopSelf();
        }
    }

//    @Override
//    public void onBackPressed() {
//        mService = (MusicService) DataCenter.instance.musicService;
//
//        if (mIsBound) {
//            unbindService(connection);
//            mIsBound = false;
//        }
//        if (mService.isPlaying()) {
//            DataCenter.instance.mainActivity = null;
//        } else {
//            mService.stopSelf();
//        }
//
//        android.os.Process.killProcess(android.os.Process.myPid());
//
//    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("123", "onResume: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("123", "onRestart: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("123", "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("123", "onStop: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("123", "onStart: ");
    }
}
