package com.example.tomato.oceanmusic.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.tomato.oceanmusic.services.MusicService;
import com.example.tomato.oceanmusic.utils.DataCenter;


/**
 * Created by IceMan on 11/29/2016.
 */

public class NextMusicReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MusicService musicService = (MusicService) DataCenter.instance.musicService;
        if (musicService != null) {
            musicService.nextMusic();
            musicService.showNotification();
        }
    }
}
