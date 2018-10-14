package com.example.tomato.oceanmusic.fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tomato.oceanmusic.R;
import com.example.tomato.oceanmusic.activities.MainActivity;
import com.example.tomato.oceanmusic.activities.PlayMusicActivity;
import com.example.tomato.oceanmusic.models.Song;
import com.example.tomato.oceanmusic.services.MusicService;
import com.example.tomato.oceanmusic.utils.Constants;
import com.example.tomato.oceanmusic.utils.DataCenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class FragmentPlayingBar extends Fragment implements View.OnClickListener {

    MusicService mService;
    View view;
    LinearLayout llPlayingBar;
    SeekBar sbSong;
    TextView tvTimePlay, tvTimeTotal, tvSongTitle, tvSongArtist;
    ImageView ivSong, ivPlayPause;
    ArrayList<Song> arrSong;

    int mPosition = -1;


    BroadcastReceiver broadcastReceiverUpdatePlaying = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mService = (MusicService) DataCenter.instance.musicService;
            mPosition = mService.getPosition();
            updatePlayingBar(mPosition);
            updatePlayPauseButton();
        }
    };

    private void registerBroadcastUpdatePlaying() {
        IntentFilter intentFilter = new IntentFilter(Constants.ACTION_COMPLETE_SONG);
        getActivity().registerReceiver(broadcastReceiverUpdatePlaying, intentFilter);
    }

    private void unRegisterBroadcastUpdatePlaying() {
        getActivity().unregisterReceiver(broadcastReceiverUpdatePlaying);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_playing_bar, container, false);

        mService = (MusicService) DataCenter.instance.musicService;
        DataCenter.instance.fmPlayingBar = this;

        initView();
        initEvent();

        if (mService != null) {
            mPosition = mService.getPosition();
            registerBroadcastUpdatePlaying();
            if (mPosition > -1) {
                updatePlayingBar(mPosition);
            }
        } else {
            registerBroadcastUpdatePlaying();
        }
        return view;
    }

    public void initView() {
        arrSong = new ArrayList<>();
        llPlayingBar = view.findViewById(R.id.ll_playing_bar);
        sbSong = view.findViewById(R.id.sb_song);
        tvTimePlay = view.findViewById(R.id.tv_time_played);
        tvTimeTotal = view.findViewById(R.id.tv_time_total);
        tvSongTitle = view.findViewById(R.id.tv_song_title);
        tvSongArtist = view.findViewById(R.id.tv_song_artist);
        ivSong = view.findViewById(R.id.iv_song);
        ivPlayPause = view.findViewById(R.id.iv_play_pause);
    }

    public void initEvent() {
        ivSong.setOnClickListener(this);
        llPlayingBar.setOnClickListener(this);
        ivPlayPause.setOnClickListener(this);
        sbSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mService.seekTo(seekBar.getProgress());
            }
        });
    }

    public void updatePlayPauseButton() {
        if (mService != null) {
            if (mService.isPlaying()) {
                ivPlayPause.setImageResource(R.drawable.pb_play);
            } else {
                ivPlayPause.setImageResource(R.drawable.pb_pause);
            }
        }
    }

    private void setTimeTotal() {
        SimpleDateFormat dinhDangGio = new SimpleDateFormat("mm:ss");
        tvTimeTotal.setText(dinhDangGio.format(mService.getDurationMedia()));

        sbSong.setMax(mService.getDurationMedia());
    }


    private void updateTimeSong() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat dinhDangGio = new SimpleDateFormat("mm:ss");
                tvTimePlay.setText(dinhDangGio.format(mService.getCurrentMedia()));

                sbSong.setProgress(mService.getCurrentMedia());

                mService.nextAutoPlayMusic();

                handler.postDelayed(this, 500);
            }
        }, 100);

    }

    public void updatePlayingBar(int position) {
        arrSong = mService.getArrSong();

        Bitmap bitmap;
        String albumPath;

        albumPath = arrSong.get(position).getAlbumImagePath();
        if (albumPath != null && albumPath != "") {
            bitmap = BitmapFactory.decodeFile(albumPath);

        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.adele);
        }
        ivSong.setImageBitmap(bitmap);
        tvSongTitle.setText(arrSong.get(position).getTitle());
        tvSongArtist.setText(arrSong.get(position).getArtist());

        updateTimeSong();
        setTimeTotal();
        updatePlayPauseButton();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_playing_bar:
                Intent intent = new Intent(getActivity(), PlayMusicActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_play_pause:
                mService = (MusicService) DataCenter.instance.musicService;
                if (mService.getStatusPlayPause()) {
                    mService.playPauseMusic();
                    updatePlayPauseButton();
                }
                break;
            case R.id.iv_song:
                Intent intent1 = new Intent(getActivity(), PlayMusicActivity.class);
                startActivity(intent1);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DataCenter.instance.fmPlayingBar = null;
        unRegisterBroadcastUpdatePlaying();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPosition > -1) {
            updatePlayingBar(mPosition);
        }
    }
}
