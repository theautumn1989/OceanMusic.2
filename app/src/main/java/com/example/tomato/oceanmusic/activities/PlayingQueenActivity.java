package com.example.tomato.oceanmusic.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;


import com.example.tomato.oceanmusic.R;
import com.example.tomato.oceanmusic.adapter.SongListPlayingAdapter;
import com.example.tomato.oceanmusic.fragments.FragmentPlayingBar;
import com.example.tomato.oceanmusic.interfaces.ItemTouchListener;
import com.example.tomato.oceanmusic.interfaces.SongPlayingOnCallBack;
import com.example.tomato.oceanmusic.models.Song;
import com.example.tomato.oceanmusic.services.MusicService;
import com.example.tomato.oceanmusic.utils.DataCenter;
import com.example.tomato.oceanmusic.utils.ItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.Collections;

public class PlayingQueenActivity extends AppCompatActivity implements SongPlayingOnCallBack {

    FragmentPlayingBar fmPlayingBar;
    SongListPlayingAdapter adapter;
    RecyclerView recyclerView;
    MusicService mService;
    ArrayList<Song> arrSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing_queen);
        updateListSong();
        init();

        addItemTouchCallback(recyclerView);
    }

    private void updateListSong() {
        mService = (MusicService) DataCenter.instance.musicService;
        if (mService != null) {
            arrSong = mService.getArrSong();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateListSong();
    }

    private void init() {
        fmPlayingBar = (FragmentPlayingBar) getFragmentManager().findFragmentById(R.id.fm_playing_bar);
        recyclerView = findViewById(R.id.rv_playing_queue);
        adapter = new SongListPlayingAdapter(PlayingQueenActivity.this, arrSong, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void addItemTouchCallback(RecyclerView recyclerView) {
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(new ItemTouchListener() {
            @Override
            public void onMove(int oldPosition, int newPosition) {
                onMoveSong(oldPosition, newPosition);
                mService.setArrSong(arrSong);
                mService.setPosition(newPosition);
            }

            @Override
            public void swipe(int position, int direction) {
                onSwipeSong(position, direction);
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void onMoveSong(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(arrSong, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(arrSong, i, i - 1);
            }
        }
        adapter.notifyItemMoved(fromPosition, toPosition);
    }

    public void onSwipeSong(int position, int direction) {
        arrSong.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onItemClicked(int position, boolean isLongClick) {
        mService = (MusicService) DataCenter.instance.musicService;
        if (mService != null) {
            mService.setPosition(position);
            mService.playMusic(position);
        }
    }
}
