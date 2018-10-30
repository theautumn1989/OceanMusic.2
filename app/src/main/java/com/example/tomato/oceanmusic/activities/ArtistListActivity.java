package com.example.tomato.oceanmusic.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

    public static final int DATA_TYPE_SONG_OF_ARTIST = 3;

    FragmentPlayingBar fmPlayingBar;

    TextView tvArtistName;
    RecyclerView rvListSong;
    SongListAdapter mSongAdapter;
    ArrayList<Song> mListSong;
    ImageView ivArtist;

    MusicService mService;
    boolean statusSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        initToolbar();
        initViews();
        getDataFromIntentAndShow();
    }

    public void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_artist);
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.abc_ic_menu_moreoverflow_mtrl_alpha));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataFromIntentAndShow();
    }

    private void getDataFromIntentAndShow() {

        mService = (MusicService) DataCenter.instance.musicService;
        if (mService != null) {
            int idArtist = mService.getIDArtist();
            mListSong = DataCenter.instance.getListSongOfArtist(idArtist);
        }

        if (mListSong != null && mListSong.size() > 0) {
            String path = mListSong.get(0).getAlbumImagePath();
            if (path != null) {
                Glide.with(this).load(path).into(ivArtist);

            } else {
                ivArtist.setImageResource(R.drawable.bg_playing_3);
            }
            mSongAdapter = new SongListAdapter(this, mListSong, this);
            rvListSong.setAdapter(mSongAdapter);
            tvArtistName.setText(mListSong.get(0).getArtist());
        }
    }

    private void initViews() {
        fmPlayingBar = (FragmentPlayingBar) getFragmentManager().findFragmentById(R.id.fm_playing_bar);
        tvArtistName = findViewById(R.id.artist_name_toolbar);
        rvListSong = findViewById(R.id.rv_artist_list_play);
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
        if (mService != null) {
            mService.setmType(DATA_TYPE_SONG_OF_ARTIST);
            mService.updateData(DATA_TYPE_SONG_OF_ARTIST);
            mService.setStatusRepeat(false);
            if (statusSearch) {
                String id = mListSong.get(position).getId();
                mService.getPositionToSearch(id);

                updateSearchActivity(false);
                getDataFromIntentAndShow();
                initToolbar();

            } else {
                mService.playMusic(position);
            }
        }
    }

    @SuppressLint("RestrictedApi")
    public void updateSearchActivity(boolean status) {
        if (status) {
            ivArtist.setVisibility(View.GONE);
        } else {
            ivArtist.setVisibility(View.VISIBLE);
            hideSoftKeyboard(this);
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
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
        setmListSong(filteredSongList);
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
                getDataFromIntentAndShow();
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
                mSongAdapter.setFilter(filter(mListSong, s));
                if (s.length() == 0) {
                    getDataFromIntentAndShow();
                }
                return false;
            }

        });
        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                statusSearch = true;
                mSongAdapter.setFilter(mListSong);
                updateSearchActivity(true);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                statusSearch = false;
                getDataFromIntentAndShow();
                updateSearchActivity(false);
                return true;
            }
        });
        return true;
    }

    public void setmListSong(ArrayList<Song> mListSong) {
        this.mListSong = mListSong;
    }

}
