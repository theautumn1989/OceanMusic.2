package com.example.tomato.oceanmusic.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.tomato.oceanmusic.activities.PlayingActivity;
import com.example.tomato.oceanmusic.fragments.FragmentPlayingBar;
import com.example.tomato.oceanmusic.models.Song;
import com.example.tomato.oceanmusic.services.MusicService;
import com.example.tomato.oceanmusic.utils.DataCenter;

import java.util.ArrayList;

public class SongCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PlayingActivity musicActivity = (PlayingActivity) DataCenter.instance.playActivity;
        MusicService mService = (MusicService) DataCenter.instance.musicService;
        FragmentPlayingBar fmPlayingBar = DataCenter.instance.fmPlayingBar;

        if (mService != null) {
            int mPosition = mService.getPosition();
            ArrayList<Song> arrSong = mService.getArrSong();

            if (musicActivity != null) {
                musicActivity.mService = mService;
                musicActivity.setmPosition(mPosition);
                musicActivity.arrSong = arrSong;
                musicActivity.updateToolbar(mPosition);
                musicActivity.updateTimeSong();
            }
            if (fmPlayingBar != null) {
                fmPlayingBar.mService = mService;
                fmPlayingBar.mPosition = mPosition;
                fmPlayingBar.updatePlayingBar(mPosition);
                fmPlayingBar.updatePlayPauseButton();
            }

        }
    }
}
