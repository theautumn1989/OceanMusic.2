package com.example.tomato.oceanmusic.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;


import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ImageView;


import com.example.tomato.oceanmusic.R;
import com.example.tomato.oceanmusic.activities.MainActivity;
import com.example.tomato.oceanmusic.adapter.SongListAdapter;
import com.example.tomato.oceanmusic.interfaces.SongOnCallBack;
import com.example.tomato.oceanmusic.models.Song;
import com.example.tomato.oceanmusic.services.MusicService;
import com.example.tomato.oceanmusic.utils.DataCenter;

import java.util.ArrayList;


public class FragmentSongList extends Fragment implements SongOnCallBack {

    public static final int COLUMS_RECYCLER = 2;
    public static final int DATA_TYPE_SONG_ALL = 1;

    View view;
    RecyclerView rvListSong;
    ArrayList<Song> listSong;
    SongListAdapter songAdapter;
    MusicService musicService;
    boolean statusSearch = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_song_list, container, false);
        initViews();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showListSong();
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
        if (listSong != null && listSong.size() > 0) {
            songAdapter = new SongListAdapter(getActivity(), listSong, this);
            rvListSong.setAdapter(songAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        RecyclerView.LayoutManager layoutManager;
        switch (item.getItemId()) {
            case R.id.list:
                layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                rvListSong.setLayoutManager(layoutManager);
                rvListSong.setHasFixedSize(true);
                break;
            case R.id.gird:
                layoutManager = new GridLayoutManager(getActivity(), COLUMS_RECYCLER);
                rvListSong.setLayoutManager(layoutManager);
                rvListSong.setHasFixedSize(true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_detail, menu);
        MenuItem item = menu.findItem(R.id.action_search_detail);
        SearchView searchView = (SearchView) item.getActionView();

        final EditText searchPlate = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchPlate.setHint(getString(R.string.search_toolbar));
        searchPlate.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        searchPlate.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

        ImageView ivClose = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        ivClose.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white));

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showListSong();
                searchPlate.setText("");
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true; //do the default
            }

            @Override
            public boolean onQueryTextChange(String s) {
                songAdapter.setFilter(filter(listSong, s));
                if (s.length() == 0) {
                    showListSong();
                }
                return false;
            }

        });
        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                statusSearch = true;
                songAdapter.setFilter(listSong);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                statusSearch = false;
                showListSong();
                return true;
            }
        });
    }

    private ArrayList<Song> filter(ArrayList<Song> lstSong, String query) {
        query = query.toLowerCase();
        ArrayList<Song> filteredSongList = new ArrayList<>();

        for (Song song : lstSong) {
            String text = song.getTitle().toLowerCase();
            if (text.contains(query)) {
                filteredSongList.add(song);
            }
        }
        setListSong(filteredSongList);
        return filteredSongList;
    }

    @Override
    public void onItemClicked(int position, boolean isLongClick) {
        MusicService mService = (MusicService) DataCenter.instance.musicService;
        if (mService != null) {
            mService.setStatusRepeat(false);
            mService.setmType(DATA_TYPE_SONG_ALL);
            mService.updateData(DATA_TYPE_SONG_ALL);
            if (statusSearch) {
                String id = listSong.get(position).getId();
                mService.getPositionToSearch(id);

                MainActivity main = (MainActivity) DataCenter.instance.mainActivity;
                if (main != null) {
                    main.initToolbar();
                    showListSong();
                }

            } else {
                mService.playMusic(position);
            }
        }
    }

    public void setListSong(ArrayList<Song> listSong) {
        this.listSong = listSong;
    }
}
