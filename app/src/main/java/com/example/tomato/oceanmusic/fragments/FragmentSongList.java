package com.example.tomato.oceanmusic.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.tomato.oceanmusic.R;
import com.example.tomato.oceanmusic.activities.MainActivity;
import com.example.tomato.oceanmusic.adapter.SongListAdapter;
import com.example.tomato.oceanmusic.interfaces.SongOnCallBack;
import com.example.tomato.oceanmusic.models.Song;
import com.example.tomato.oceanmusic.services.MusicService;
import com.example.tomato.oceanmusic.utils.DataCenter;

import java.util.ArrayList;


public class FragmentSongList extends Fragment implements SongOnCallBack {

    View view;
    RecyclerView rvListSong;
    ArrayList<Song> listSong;
    SongListAdapter songAdapter;
    MusicService musicService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_song_list, container, false);
        initViews();
        showListSong();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void initViews() {
        rvListSong = view.findViewById(R.id.rv_song_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvListSong.setLayoutManager(layoutManager);
        rvListSong.setHasFixedSize(true);
        musicService = new MusicService();
    }

    private void showListSong() {
        listSong = DataCenter.instance.getListSong();

        songAdapter = new SongListAdapter(getActivity(), listSong, this);
        rvListSong.setAdapter(songAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_detail, menu);
        MenuItem item = menu.findItem(R.id.action_search_detail);

        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                songAdapter.setFilter(listSong);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });
    }

    @Override
    public void onItemClicked(int position, boolean isLongClick) {

        MusicService mService = (MusicService) DataCenter.instance.musicService;
        mService.setStatusRepeat(false);
        mService.setmType(1);
        mService.updateData(1);
        mService.playMusic(position);
    }
}
