package com.example.tomato.oceanmusic.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

    public static final int DATA_TYPE_SONG_OF_ALBUM = 2;

    FragmentPlayingBar fmPlayingBar;
    MusicService mService;
    RecyclerView rvSongList;
    ImageView ivAlbumCover;
    ArrayList<Song> listSong;
    SongListAdapter adapter;
    TextView tvAlbumTitle;
    boolean statusSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        initToolbar();
        initViews();
        getAndShowSongList();
        showCover();
    }

    @Override
    public void onResume() {
        super.onResume();
        getAndShowSongList();
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

        tvAlbumTitle = findViewById(R.id.tv_album_title);
        ivAlbumCover = findViewById(R.id.iv_album_list_play);
    }

    private void showCover() {
        if (listSong != null) {
            String path = listSong.get(0).getAlbumImagePath();
            if (path != null) {
                File file = new File(path);
                Uri uri = Uri.fromFile(file);
                ivAlbumCover.setImageURI(uri);
            }
            tvAlbumTitle.setText(listSong.get(0).getAlbum());
        }
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
        mService.setmType(DATA_TYPE_SONG_OF_ALBUM);
        mService.updateData(DATA_TYPE_SONG_OF_ALBUM);
        mService.setStatusRepeat(false);
        if (statusSearch) {
            String id = listSong.get(position).getId();
            mService.getPositionToSearch(id);

        } else {
            mService.playMusic(position);
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_playing, menu);
        MenuItem item = menu.findItem(R.id.ic_search_playing);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        final EditText searchPlate = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchPlate.setHint(R.string.search_toolbar);
        searchPlate.setHintTextColor(ContextCompat.getColor(this, R.color.white));
        searchPlate.setTextColor(ContextCompat.getColor(this, R.color.white));

        ImageView ivClose = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        ivClose.setColorFilter(ContextCompat.getColor(this, R.color.white));

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAndShowSongList();
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
                adapter.setFilter(filter(listSong, s));
                if (s.length() == 0) {
                    getAndShowSongList();
                }
                return false;
            }

        });
        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                statusSearch = true;
                adapter.setFilter(listSong);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                statusSearch = false;
                getAndShowSongList();
                return true;
            }
        });
        return true;
    }

    public void setListSong(ArrayList<Song> listSong) {
        this.listSong = listSong;
    }

}
