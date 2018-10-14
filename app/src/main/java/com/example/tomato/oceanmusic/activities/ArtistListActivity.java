package com.example.tomato.oceanmusic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tomato.oceanmusic.R;
import com.example.tomato.oceanmusic.adapter.SongListAdapter;
import com.example.tomato.oceanmusic.fragments.FragmentPlayingBar;
import com.example.tomato.oceanmusic.interfaces.SongOnCallBack;
import com.example.tomato.oceanmusic.models.Song;
import com.example.tomato.oceanmusic.services.MusicService;
import com.example.tomato.oceanmusic.utils.DataCenter;


import java.util.ArrayList;

public class ArtistListActivity extends AppCompatActivity implements SongOnCallBack {

    FragmentPlayingBar fmPlayingBar;

    TextView tvArtistName;
    RecyclerView rvListSong;
    SongListAdapter mSongAdapter;
    ArrayList<Song> mListSong;
    ImageView ivBackGround;
    ImageView ivArtist;

    MusicService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        Toolbar toolbar = findViewById(R.id.toolbar_artist);
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.abc_ic_menu_moreoverflow_mtrl_alpha));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();

        getDataFromIntentAndShow();
        // DataCenter.instance.setDefaultWallpaper(ivBackGround);
    }

    private void getDataFromIntentAndShow() {

        mService = (MusicService) DataCenter.instance.musicService;
        int idArtist = mService.getIDArtist();
        mListSong = DataCenter.instance.getListSongOfArtist(idArtist);
        String path = mListSong.get(0).getAlbumImagePath();
        if (path != null) {
            Glide.with(this).load(path).into(ivArtist);

        } else {
            ivArtist.setImageResource(R.drawable.stop);
        }

        mSongAdapter = new SongListAdapter(this, mListSong, this);
        rvListSong.setAdapter(mSongAdapter);
        tvArtistName.setText(mListSong.get(0).getArtist());
    }

    private void initViews() {
        fmPlayingBar = (FragmentPlayingBar) getFragmentManager().findFragmentById(R.id.fm_playing_bar);
        tvArtistName = findViewById(R.id.artist_name_toolbar);
        rvListSong = findViewById(R.id.rv_artist_list_play);
        ivBackGround = findViewById(R.id.iv_back_ground_artist);
        ivArtist = findViewById(R.id.iv_artist);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvListSong.setLayoutManager(layoutManager);
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
        mService.setmType(3);
        mService.updateData(3);
        mService.playMusic(position);
        mService.setStatusRepeat(false);
    }
}
