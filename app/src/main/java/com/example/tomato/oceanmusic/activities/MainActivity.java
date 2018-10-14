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

    public static final int PERMISSION_KEY = 1989;

    FragmentPlayingBar fmPlayingBar;
    MusicService mService;
    int mPosition = -1;

    ArrayList<Song> arrSong;
    ViewPager vpMain;
    TabLayout tabMain;
    Button btnPlay;
    boolean statusPlayApp = false;
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
        btnPlay = findViewById(R.id.btn_play);
        mService = new MusicService();
        mService = (MusicService) DataCenter.instance.musicService;

        if (mService != null) {
            mPosition = mService.getPosition();
            statusPlayApp = mService.getStatusPlayApp();

        } else {
            initService();
        }
        DataCenter.instance.mainActivity = this;

        if (statusPlayApp) {

            initToolbar();
            initView();
            init();
            btnPlay.setVisibility(View.INVISIBLE);
        }

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initToolbar();
                initView();
                init();
                mService.setStatusPlayApp(true);
                mService.updateData(1);
                mService.setmType(1);
                btnPlay.setVisibility(View.INVISIBLE);
            }
        });

    }

    public void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //Permisson don't granted
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                }
                // Permisson don't granted and dont show dialog again.
                else {
                }
                //Register permission
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_KEY);
            }
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
                } else {
                }
                return;
            }
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }


    private void init() {
        ViewPagerMainAdapter adapter = new ViewPagerMainAdapter(getSupportFragmentManager(), this);
        arrSong = new ArrayList<>();
        arrSong = DataCenter.instance.getListSong();
        vpMain.setAdapter(adapter);
        tabMain.setupWithViewPager(vpMain);
        vpMain.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabMain));
        vpMain.setOffscreenPageLimit(3);
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
        if (mService.getStatusForegound()) {
            DataCenter.instance.mainActivity = null;
        } else {
            mService.stopSelf();
        }
        if (mIsBound) {
            unbindService(connection);
            mIsBound = false;
        }

    }
}
