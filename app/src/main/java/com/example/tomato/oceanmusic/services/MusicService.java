package com.example.tomato.oceanmusic.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;


import com.example.tomato.oceanmusic.R;
import com.example.tomato.oceanmusic.activities.MainActivity;
import com.example.tomato.oceanmusic.activities.PlayMusicActivity;
import com.example.tomato.oceanmusic.fragments.FragmentPlayingBar;
import com.example.tomato.oceanmusic.models.Song;
import com.example.tomato.oceanmusic.utils.Constants;
import com.example.tomato.oceanmusic.utils.DataCenter;

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
        Log.d("MusicServiceMedia","Create");
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
                arrSong = DataCenter.instance.getListSongOfAlbum(IDAlbum);
                break;
            case DATA_TYPE_SONG_OF_ARSITS:
                arrSong = DataCenter.instance.getListSongOfArtist(IDArtist);
                break;
            default:
                break;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (ACTION_STOP_SERVICE.equals(intent.getAction())) {
            PlayMusicActivity musicActivity = (PlayMusicActivity) DataCenter.instance.playActivity;
            MainActivity mainActivity = (MainActivity) DataCenter.instance.mainActivity;
            FragmentPlayingBar fmPlayingBar = DataCenter.instance.fmPlayingBar;

            if (musicActivity == null && mainActivity == null) {
                stopSelf();
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
        mediaPlayer = MediaPlayer.create(this, Uri.parse("file://" + arrSong.get(position).getPath()));
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        Intent intent = new Intent(Constants.ACTION_COMPLETE_SONG);
        sendBroadcast(intent);

        showNotification(true);

        if (!getStatusPlayPause()) {
            setStatusPlayPause(true);
        }
    }

    public void nextAutoPlayMusic() {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {    // phương thức kiểm tra khi kết thúc bài hát
            @Override
            public void onCompletion(MediaPlayer mp) {
                nextMusic();

                Intent intent = new Intent(Constants.ACTION_COMPLETE_SONG);
                sendBroadcast(intent);
            }
        });
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
                Log.d("1234", "releaseMusic: ");
                stopMusic();
                mediaPlayer.release();
            }
        }catch (IllegalStateException e){

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

    public void backMusic() {
        if (isShuffle) {
            Random r = new Random();
            int newPosition = r.nextInt(arrSong.size());
            mPosition = newPosition;
        } else {
            if (mPosition == 0) {
                mPosition = arrSong.size();
            } else {
                mPosition--;
            }
        }
        playMusic(mPosition);
        setPosition(mPosition);
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

    public Notification showNotification(boolean isUpdate) {
        bigViews = new RemoteViews(getPackageName(), R.layout.notification_view_expanded);
        views = new RemoteViews(getPackageName(), R.layout.notification_view);
        Intent intent = new Intent(getApplicationContext(), PlayMusicActivity.class);
        intent.putExtra(PlayMusicActivity.BACK, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (isPlaying()) {
            views.setImageViewResource(R.id.btn_play_pause_noti, R.drawable.ic_pause_new);
            bigViews.setImageViewResource(R.id.btn_play_pause_noti, R.drawable.ic_pause_new);
        } else {
            views.setImageViewResource(R.id.btn_play_pause_noti, R.drawable.ic_play_new);
            bigViews.setImageViewResource(R.id.btn_play_pause_noti, R.drawable.ic_play_new);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentPrev = new Intent(Constants.ACTION_PREV);
        intentPrev.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntentPrev = PendingIntent.getBroadcast(getApplicationContext(), 0, intentPrev, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentPlayPause = new Intent(Constants.ACTION_PLAY_PAUSE);
        intentPlayPause.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntentPlayPause = PendingIntent.getBroadcast(getApplicationContext(), 0, intentPlayPause, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentNext = new Intent(Constants.ACTION_NEXT);
        intentNext.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntentNext = PendingIntent.getBroadcast(getApplicationContext(), 0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentStopSelf = new Intent(this, MusicService.class);
        intentStopSelf.setAction(MusicService.ACTION_STOP_SERVICE);
        PendingIntent pendingIntentStopSelf = PendingIntent.getService(this, 0, intentStopSelf, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(pendingIntent);
        builder.setContent(views);
        builder.setCustomBigContentView(bigViews);

        if (arrSong != null) {
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

        n = builder.build();
        bigViews.setOnClickPendingIntent(R.id.btn_close_noti, pendingIntentStopSelf);
        bigViews.setOnClickPendingIntent(R.id.btn_prev_noti, pendingIntentPrev);
        bigViews.setOnClickPendingIntent(R.id.btn_next_noti, pendingIntentNext);
        bigViews.setOnClickPendingIntent(R.id.btn_play_pause_noti, pendingIntentPlayPause);

        views.setOnClickPendingIntent(R.id.btn_close_noti, pendingIntentStopSelf);
        views.setOnClickPendingIntent(R.id.btn_next_noti, pendingIntentNext);
        views.setOnClickPendingIntent(R.id.btn_play_pause_noti, pendingIntentPlayPause);

        if (isUpdate) {
            startForeground(NOTIFICATION_ID, n);
            statusForeground = true;
        }
        return n;
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }

    public int getCurrentMedia() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDurationMedia() {
        return mediaPlayer.getDuration();
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
