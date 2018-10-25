package com.example.tomato.oceanmusic.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.tomato.oceanmusic.R;
import com.example.tomato.oceanmusic.adapter.SongListPlayingAdapter;
import com.example.tomato.oceanmusic.interfaces.SongPlayingOnCallBack;
import com.example.tomato.oceanmusic.models.Song;
import com.example.tomato.oceanmusic.services.MusicService;
import com.example.tomato.oceanmusic.utils.CircularSeekBar;
import com.example.tomato.oceanmusic.utils.Constants;
import com.example.tomato.oceanmusic.utils.DataCenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PlayMusicActivity extends AppCompatActivity
        implements View.OnClickListener, SongPlayingOnCallBack {

    public static final String BACK = "back";
    boolean statusSearch = false;
    FloatingActionButton float_play_pause;
    ImageView ivNext, ivpre, ivRepeat, ivShuffle;
    RecyclerView rvListSongPlaying;
    LinearLayoutManager layoutManager;
    ArrayList<Song> arrSong;
    SongListPlayingAdapter songAdapter;

    CoordinatorLayout coordinatorLayout;
    CircularSeekBar circularSeekBar;
    MusicService mService;

    RelativeLayout rlMediaControls;
    TextView tvTimeCenter, mTvSongName, mTvArtist;
    int mPosition = 0;
    boolean isSeeking;

    BroadcastReceiver broadcastReceiverSongCompleted = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mService = (MusicService) DataCenter.instance.musicService;
            mPosition = mService.getPosition();
            arrSong = mService.getArrSong();
            updateToolbar(mPosition);
            updateTimeSong();
        }
    };

    private void registerBroadcastSongComplete() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_COMPLETE_SONG);
        registerReceiver(broadcastReceiverSongCompleted, intentFilter);
    }

    private void unRegisterBroadcastSongComplete() {
        unregisterReceiver(broadcastReceiverSongCompleted);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        DataCenter.instance.playActivity = this;
        initToolbar();

        initView();
        init();
        initEvent();

        showListSong();
        updateToolbar(mPosition);
        updatePlayPauseButton();
        updateTimeSong();
        registerBroadcastSongComplete();
        updateStatusRepeatShuffle();
        setAlbumArt();

    }

    @Override
    protected void onStart() {
        super.onStart();
        arrSong = mService.getArrSong();
        showListSong();
        setAlbumArt();
    }

    public void updateStatusRepeatShuffle() {
        if (mService.isRepeat()) {
            ivRepeat.setImageResource(R.drawable.ic_widget_repeat_one);
        } else {
            ivRepeat.setImageResource(R.drawable.ic_widget_repeat_off);
        }

        if (mService.isShuffle()) {
            ivShuffle.setImageResource(R.drawable.ic_widget_shuffle_on);
        } else {
            ivShuffle.setImageResource(R.drawable.ic_widget_shuffle_off);
        }
    }

    public void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_playing);
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.abc_ic_menu_moreoverflow_mtrl_alpha));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void init() {
        arrSong = new ArrayList<>();
        mService = (MusicService) DataCenter.instance.musicService;
        mPosition = mService.getPosition();
        arrSong = mService.getArrSong();
    }

    private void showListSong() {
        songAdapter = new SongListPlayingAdapter(this, arrSong, this);
        rvListSongPlaying.setAdapter(songAdapter);
        rvListSongPlaying.setHasFixedSize(true);
    }

    private void initEvent() {
        float_play_pause.setOnClickListener(this);
        ivShuffle.setOnClickListener(this);
        ivRepeat.setOnClickListener(this);
        ivpre.setOnClickListener(this);
        ivNext.setOnClickListener(this);

        circularSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
                mService.seekTo(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
            }
        });
    }

    private void initView() {
        float_play_pause = findViewById(R.id.fab_play_pause);
        mTvSongName = findViewById(R.id.tv_song_name_play);
        mTvArtist = findViewById(R.id.tv_artist_play);

        coordinatorLayout = findViewById(R.id.coordinator_playing);
        circularSeekBar = findViewById(R.id.circularSb);

        ivpre = findViewById(R.id.iv_prev);
        ivNext = findViewById(R.id.iv_next);
        ivShuffle = findViewById(R.id.iv_shuffle);
        ivRepeat = findViewById(R.id.iv_repeat);

        rlMediaControls = findViewById(R.id.rl_media_controls);
        tvTimeCenter = findViewById(R.id.tv_time_center);
        isSeeking = false;
        rvListSongPlaying = findViewById(R.id.rv_song_list_playing);

        ivNext = findViewById(R.id.iv_next);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvListSongPlaying.setLayoutManager(layoutManager);

        rvListSongPlaying.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        rvListSongPlaying.addItemDecoration(dividerItemDecoration);
    }

    private void setAlbumArt() {
        if (mPosition > -1) {
            Bitmap bitmap;
            String albumPath;
            albumPath = arrSong.get(mPosition).getAlbumImagePath();
            if (albumPath != null && albumPath != "") {
                bitmap = BitmapFactory.decodeFile(albumPath);
            } else {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_player_bg);
            }
            BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
            coordinatorLayout.setBackground(bitmapDrawable);
        }
    }

    private void updateToolbar(int position) {
        if (position > -1) {
            mTvSongName.setText(arrSong.get(position).getTitle());
            mTvArtist.setText(arrSong.get(position).getArtist());
        }
    }

    public void updatePlayPauseButton() {
        if (mService != null) {
            if (mService.isPlaying()) {
                float_play_pause.setImageResource(R.drawable.ic_pause_new);
            } else {
                float_play_pause.setImageResource(R.drawable.ic_play_new);
            }
        }
    }

    private void updateTimeSong() {
        circularSeekBar.setMax(mService.getDurationMedia());
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat dinhDangGio = new SimpleDateFormat("mm:ss");
                tvTimeCenter.setText(dinhDangGio.format(mService.getCurrentMedia()));

                circularSeekBar.setProgress(mService.getCurrentMedia());

                mService.nextAutoPlayMusic();

                handler.postDelayed(this, 500);
            }
        }, 100);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_play_pause:
                if (mService.getStatusPlayPause()) {
                    mService.playPauseMusic();
                    updatePlayPauseButton();
                }
                break;
            case R.id.iv_next:
                mService.nextMusic();
                setAlbumArt();
                break;
            case R.id.iv_prev:
                mService.backMusic();
                setAlbumArt();
                break;
            case R.id.iv_repeat:
                if (mService.isRepeat()) {
                    ivRepeat.setImageResource(R.drawable.ic_widget_repeat_off);
                    mService.setRepeat(false);
                } else {
                    ivRepeat.setImageResource(R.drawable.ic_widget_repeat_one);
                    mService.setRepeat(true);
                }
                break;
            case R.id.iv_shuffle:
                if (mService == null) return;
                if (mService.isShuffle()) {
                    ivShuffle.setImageResource(R.drawable.ic_widget_shuffle_off);
                    mService.setShuffle(false);
                } else {
                    ivShuffle.setImageResource(R.drawable.ic_widget_shuffle_on);
                    mService.setShuffle(true);
                }
                break;
        }
    }

    @Override
    public void onItemClicked(int position, boolean isLongClick) {

        mService.setStatusRepeat(false);
        updatePlayPauseButton();
        updateToolbar(position);
        setAlbumArt();
        if (statusSearch) {
            String id = arrSong.get(position).getId();
            mService.getPositionToSearch(id);

        } else {
            mService.playMusic(position);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBroadcastSongComplete();

        mService = (MusicService) DataCenter.instance.musicService;
        if (mService.getStatusForegound()) {
            DataCenter.instance.playActivity = null;
        } else {
            mService.stopSelf();
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
        setArrSong(filteredSongList);
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
                songAdapter.setFilter(filter(arrSong, s));
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
                songAdapter.setFilter(arrSong);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                statusSearch = false;
                showListSong();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.it_queue:
                startActivity(new Intent(this, PlayingQueenActivity.class));
                break;
            case android.R.id.home:
                MainActivity main = (MainActivity) DataCenter.instance.mainActivity;
                if (main != null) {
                    onBackPressed();

                } else {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            MainActivity main = (MainActivity) DataCenter.instance.mainActivity;
            if (main != null) {
                onBackPressed();

            } else {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setArrSong(ArrayList<Song> arrSong) {
        this.arrSong = arrSong;
    }
}
