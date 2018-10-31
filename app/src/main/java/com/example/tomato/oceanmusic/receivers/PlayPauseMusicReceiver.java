package com.example.tomato.oceanmusic.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.tomato.oceanmusic.activities.PlayingActivity;
import com.example.tomato.oceanmusic.fragments.FragmentPlayingBar;
import com.example.tomato.oceanmusic.services.MusicService;
import com.example.tomato.oceanmusic.utils.DataCenter;


/**
 * Created by IceMan on 11/29/2016.
 */

public class PlayPauseMusicReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PlayingActivity musicActivity = (PlayingActivity) DataCenter.instance.playActivity;
        MusicService musicService = (MusicService) DataCenter.instance.musicService;

        FragmentPlayingBar fmPlayingBar = DataCenter.instance.fmPlayingBar;
        if (musicService != null) {
            musicService.playPauseMusic();
            musicService.showNotification();
        }
        if (musicActivity != null) {
            musicActivity.updatePlayPauseButton();
        }
        if (fmPlayingBar != null) {
            fmPlayingBar.updatePlayPauseButton();
        }

    }
}
