package com.example.tomato.oceanmusic.activities;


import android.Manifest;

import android.content.ComponentName;
import android.content.Context;

import android.content.Intent;

import android.content.ServiceConnection;

import android.content.pm.PackageManager;

import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import android.support.design.widget.TabLayout;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPermission();

    }

    public void initData() {
        DataCenter.instance.mainActivity = this;
        mService = (MusicService) DataCenter.instance.musicService;

        if (mService != null) {
            mPosition = mService.getPosition();
        } else {
            initService();
        }
    }

    public void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_KEY);
            } else {
                initData();
                initToolbar();
                initView();
                init();
            }
        } else {
            initData();
            initToolbar();
            initView();
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_KEY: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    initData();
                    initToolbar();
                    initView();
                    init();
                } else {
                    //   Toast.makeText(getApplicationContext(), "người dùng không cấp quyền cho ứng dụng", Toast.LENGTH_LONG).show();
                    Toast.makeText(this, R.string.notification_permissions, Toast.LENGTH_LONG).show();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                return;
            }
        }
    }

    public void initToolbar() {
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

        mService = (MusicService) DataCenter.instance.musicService;
        if (mIsBound) {
            unbindService(connection);
            mIsBound = false;
        }
        if (mService != null && mService.isPlaying()) {
            DataCenter.instance.mainActivity = null;
        } else {
            mService.stopSelf();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initToolbar();
    }
}
