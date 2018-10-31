package com.example.tomato.oceanmusic.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;


import com.example.tomato.oceanmusic.R;
import com.example.tomato.oceanmusic.activities.MainActivity;
import com.example.tomato.oceanmusic.activities.PlayingActivity;
import com.example.tomato.oceanmusic.fragments.FragmentPlayingBar;
import com.example.tomato.oceanmusic.models.Song;
import com.example.tomato.oceanmusic.receivers.NextMusicReceiver;
import com.example.tomato.oceanmusic.receivers.PlayPauseMusicReceiver;
import com.example.tomato.oceanmusic.receivers.PrevMusicReceiver;
import com.example.tomato.oceanmusic.receivers.SongCompletedReceiver;
import com.example.tomato.oceanmusic.utils.Constants;
import com.example.tomato.oceanmusic.utils.DataCenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;


public class MusicService extends Service {

    public static final int DATA_TYPE_SONG_ALL = 1;
    public static final int DATA_TYPE_SONG_OF_ALBUM = 2;
    public static final int DATA_TYPE_SONG_OF_ARSITS = 3;
    public static final String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";
    private static final int NOTIFICATION_ID = 1609;

    int mType = 1;
    int IDAlbum;

    int IDArtist;

    private IBinder binder;
    MediaPlayer mediaPlayer;

    ArrayList<Song> arrSong;
    RemoteViews bigViews;
    RemoteViews views;
    NotificationManager notificationManager;
    String channelId = "default_channel_id";
    String channelDescription = "Default Channel";
    Notification n;

    int mPosition = -1;
    int mRepeatPosition;
    boolean statusForeground = false;
    boolean statusPlayPause = false;
    boolean isShuffle = false;
    boolean isRepeat = false;
    boolean statusRepeat = true;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MusicServiceMedia", "Create");
        mediaPlayer = new MediaPlayer();
        arrSong = new ArrayList<>();
        binder = new MyBinder();
        updateData(mType);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void getPositionToSearch(String id) {
        if (arrSong != null) {
            for (int i = 0; i < arrSong.size(); i++) {
                if (arrSong.get(i).getId().equals(id)) {
                    mPosition = i;
                }
            }
            playMusic(mPosition);
        }
    }

    public void updateData(int type) {
        switch (type) {
            case DATA_TYPE_SONG_ALL:
                arrSong = DataCenter.instance.getListSong();
                break;
            case DATA_TYPE_SONG_OF_ALBUM:
                if (IDAlbum > -1) {
                    arrSong = DataCenter.instance.getListSongOfAlbum(IDAlbum);
                }
                break;
            case DATA_TYPE_SONG_OF_ARSITS:
                if (IDArtist > -1) {
                    arrSong = DataCenter.instance.getListSongOfArtist(IDArtist);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (ACTION_STOP_SERVICE.equals(intent.getAction())) {
            if (!mediaPlayer.isPlaying()) {
                PlayingActivity musicActivity = (PlayingActivity) DataCenter.instance.playActivity;
                MainActivity mainActivity = (MainActivity) DataCenter.instance.mainActivity;
                FragmentPlayingBar fmPlayingBar = DataCenter.instance.fmPlayingBar;

                if (musicActivity == null && mainActivity == null) {
                    stopSelf();
                    android.os.Process.killProcess(android.os.Process.myPid());     // kill app
                }
                pauseMusic();
                stopForeground(true);
                statusForeground = false;

                if (fmPlayingBar != null) {
                    fmPlayingBar.updatePlayPauseButton();
                }
                if (musicActivity != null) {
                    musicActivity.updatePlayPauseButton();
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void playMusic(int position) {

        if (!getStatusPlayPause()) {
            mRepeatPosition = position;
        }
        if (isRepeat() && statusRepeat) {
            position = mRepeatPosition;
        } else {
            mRepeatPosition = position;
        }
        releaseMusic();
        mediaPlayer = new MediaPlayer();
        this.mPosition = position;
        if (arrSong != null && arrSong.size() > 0) {
            mediaPlayer = MediaPlayer.create(this, Uri.parse("file://" + arrSong.get(position).getPath()));
            // mediaPlayer.setLooping(false);
            mediaPlayer.start();
        }

        Intent intent = new Intent(getApplicationContext(), SongCompletedReceiver.class);
        sendBroadcast(intent);

        showNotification();

        if (!getStatusPlayPause()) {
            setStatusPlayPause(true);
        }
    }

    public void nextAutoPlayMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    nextMusic();

                    Intent intent = new Intent(getApplicationContext(), SongCompletedReceiver.class);
                    sendBroadcast(intent);
                }
            });
        }
    }


    public void playPauseMusic() {
        if (mediaPlayer.isPlaying()) {
            pauseMusic();
        } else {
            resumeMusic();
        }
    }

    private void releaseMusic() {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                stopMusic();
                mediaPlayer.release();
            }
        } catch (IllegalStateException e) {

        }
    }

    public void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            changePlayPauseState();
        }
    }

    public void resumeMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            changePlayPauseState();
        }
    }

    public void nextMusic() {
        statusRepeat = true;
        if (arrSong != null && arrSong.size() > 0) {
            if (isShuffle) {
                Random r = new Random();
                int newPosition = r.nextInt(arrSong.size());
                mPosition = newPosition;
            } else {
                mPosition++;
            }
            if (mPosition > arrSong.size() - 1) {
                mPosition = 0;
            }
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            playMusic(mPosition);
            setPosition(mPosition);
        }
    }


    public void backMusic() {
        if (arrSong != null && arrSong.size() > 0) {
            if (isShuffle) {
                Random r = new Random();
                int newPosition = r.nextInt(arrSong.size());
                mPosition = newPosition;
            } else {
                mPosition--;
            }
            if (mPosition < 0) {
                mPosition = arrSong.size() - 1;
            }
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            playMusic(mPosition);
            setPosition(mPosition);
        }
    }

    public void changePlayPauseState() {
        if (isPlaying()) {
            views.setImageViewResource(R.id.btn_play_pause_noti, R.drawable.ic_pause_new);
            bigViews.setImageViewResource(R.id.btn_play_pause_noti, R.drawable.ic_pause_new);
        } else {
            views.setImageViewResource(R.id.btn_play_pause_noti, R.drawable.ic_play_new);
            bigViews.setImageViewResource(R.id.btn_play_pause_noti, R.drawable.ic_play_new);
        }
        startForeground(NOTIFICATION_ID, n);
        statusForeground = true;
    }


    public class MyBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public Notification showNotification() {

        Intent intent = new Intent(getApplicationContext(), PlayingActivity.class);
        intent.putExtra(PlayingActivity.BACK, true);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        bigViews = new RemoteViews(getPackageName(), R.layout.notification_view_expanded);
        views = new RemoteViews(getPackageName(), R.layout.notification_view);

        if (arrSong != null && arrSong.size() > 0) {
            bigViews.setTextViewText(R.id.tv_song_title_noti, arrSong.get(mPosition).getTitle());
            bigViews.setTextViewText(R.id.tv_artist_noti, arrSong.get(mPosition).getArtist());

            views.setTextViewText(R.id.tv_song_title_noti, arrSong.get(mPosition).getTitle());
            views.setTextViewText(R.id.tv_artist_noti, arrSong.get(mPosition).getArtist());

            String albumPath = arrSong.get(mPosition).getAlbumImagePath();
            if (albumPath != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(albumPath);
                bigViews.setImageViewBitmap(R.id.img_album_art_noti, bitmap);
                views.setImageViewBitmap(R.id.img_album_art_noti, bitmap);
            } else {
                bigViews.setImageViewResource(R.id.img_album_art_noti, R.drawable.ic_album_new);
                views.setImageViewResource(R.id.img_album_art_noti, R.drawable.ic_album_new);
            }
        }

        if (isPlaying()) {
            views.setImageViewResource(R.id.btn_play_pause_noti, R.drawable.ic_pause_new);
            bigViews.setImageViewResource(R.id.btn_play_pause_noti, R.drawable.ic_pause_new);
        } else {
            views.setImageViewResource(R.id.btn_play_pause_noti, R.drawable.ic_play_new);
            bigViews.setImageViewResource(R.id.btn_play_pause_noti, R.drawable.ic_play_new);
        }


        Intent intentPrev = new Intent(this, PrevMusicReceiver.class);
        intentPrev.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntentPrev = PendingIntent.getBroadcast(getApplicationContext(), 0, intentPrev, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentPlayPause = new Intent(this, PlayPauseMusicReceiver.class);
        intentPlayPause.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntentPlayPause = PendingIntent.getBroadcast(getApplicationContext(), 0, intentPlayPause, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentNext = new Intent(this, NextMusicReceiver.class);
        intentNext.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntentNext = PendingIntent.getBroadcast(this, 0, intentNext, 0);

        Intent intentStopSelf = new Intent(this, MusicService.class);
        intentStopSelf.setAction(MusicService.ACTION_STOP_SERVICE);
        PendingIntent pendingIntentStopSelf = PendingIntent.getService(this, 0, intentStopSelf, PendingIntent.FLAG_UPDATE_CURRENT);

        bigViews.setOnClickPendingIntent(R.id.btn_close_noti, pendingIntentStopSelf);
        bigViews.setOnClickPendingIntent(R.id.btn_prev_noti, pendingIntentPrev);
        bigViews.setOnClickPendingIntent(R.id.btn_next_noti, pendingIntentNext);
        bigViews.setOnClickPendingIntent(R.id.btn_play_pause_noti, pendingIntentPlayPause);

        views.setOnClickPendingIntent(R.id.btn_close_noti, pendingIntentStopSelf);
        views.setOnClickPendingIntent(R.id.btn_next_noti, pendingIntentNext);
        views.setOnClickPendingIntent(R.id.btn_play_pause_noti, pendingIntentPlayPause);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelId);
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH; //Set the importance level
                notificationChannel = new NotificationChannel(channelId, channelDescription, importance);
                notificationChannel.setLightColor(Color.GREEN); //Set if it is necesssary
                notificationChannel.enableVibration(true); //Set if it is necesssary
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            n = new Notification.Builder(this, channelId).build();
        } else {
            n = new Notification.Builder(this).build();
        }
        n.bigContentView = bigViews;
        n.contentView = views;
        n.flags = Notification.FLAG_ONGOING_EVENT;
        n.icon = R.drawable.ic_notification;
        n.contentIntent = pendingIntent;

        startForeground(NOTIFICATION_ID, n);
        statusForeground = true;
        return n;
    }

    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }

    public int getCurrentMedia() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return -1;
    }

    public int getDurationMedia() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    public void seekTo(int seconds) {
        mediaPlayer.seekTo(seconds);
    }

    public boolean getStatusForegound() {
        return statusForeground;
    }

    public boolean getStatusPlayPause() {
        return statusPlayPause;
    }

    public void setStatusPlayPause(boolean statusPlayPause) {
        this.statusPlayPause = statusPlayPause;
    }

    public void setShuffle(boolean shuffle) {
        this.isShuffle = shuffle;
    }

    public boolean isShuffle() {
        return isShuffle;
    }

    public void setRepeat(boolean repeat) {
        this.isRepeat = repeat;
    }

    public boolean isRepeat() {
        return isRepeat;
    }

    public boolean isStatusRepeat() {
        return statusRepeat;
    }

    public void setStatusRepeat(boolean statusRepeat) {
        this.statusRepeat = statusRepeat;
    }

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public int getIDAlbum() {
        return IDAlbum;
    }

    public void setIDAlbum(int IDAlbum) {
        this.IDAlbum = IDAlbum;
    }

    public int getIDArtist() {
        return IDArtist;
    }

    public void setIDArtist(int IDArtist) {
        this.IDArtist = IDArtist;
    }

    public ArrayList<Song> getArrSong() {
        return arrSong;
    }

    public void setArrSong(ArrayList<Song> arrSong) {
        this.arrSong = arrSong;
    }

}
