package com.example.tomato.oceanmusic.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.tomato.oceanmusic.R;
import com.example.tomato.oceanmusic.adapter.SongListAdapter;
import com.example.tomato.oceanmusic.fragments.FragmentPlayingBar;
import com.example.tomato.oceanmusic.interfaces.SongOnCallBack;
import com.example.tomato.oceanmusic.models.Song;
import com.example.tomato.oceanmusic.services.MusicService;
import com.example.tomato.oceanmusic.utils.DataCenter;

import java.io.File;
import java.util.ArrayList;

public class AlbumListActivity extends AppCompatActivity implements SongOnCallBack {

    FragmentPlayingBar fmPlayingBar;

    MusicService mService;
    RecyclerView rvSongList;
    ImageView ivAlbumCover, ivBackGround;
    ArrayList<Song> listSong;
    SongListAdapter adapter;
    TextView tvAlbumTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        initToolbar();
        initViews();
        getAndShowSongList();
        showCover();
    }

    public void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_album_list_activity);
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.abc_ic_menu_moreoverflow_mtrl_alpha));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        fmPlayingBar = (FragmentPlayingBar) getFragmentManager().findFragmentById(R.id.fm_playing_bar);
        rvSongList = findViewById(R.id.rv_album_list_play);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvSongList.setLayoutManager(layoutManager);

        ivBackGround = findViewById(R.id.iv_back_ground_album);
        tvAlbumTitle = findViewById(R.id.tv_album_title);
        ivAlbumCover = findViewById(R.id.iv_album_list_play);
    }

    private void showCover() {
        String path = listSong.get(0).getAlbumImagePath();
        if (path != null) {
            File file = new File(path);
            Uri uri = Uri.fromFile(file);
            ivAlbumCover.setImageURI(uri);
        }
        tvAlbumTitle.setText(listSong.get(0).getAlbum());
    }

    private void getAndShowSongList() {
        mService = (MusicService) DataCenter.instance.musicService;
        int idAlbum = mService.getIDAlbum();
        listSong = DataCenter.instance.getListSongOfAlbum(idAlbum);
        adapter = new SongListAdapter(this, listSong, this);
        rvSongList.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(int position, boolean isLongClick) {
        mService = (MusicService) DataCenter.instance.musicService;
        mService.setmType(2);
        mService.updateData(2);
        mService.playMusic(position);
        mService.setStatusRepeat(false);
    }
}
