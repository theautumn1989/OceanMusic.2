package com.example.tomato.oceanmusic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tomato.oceanmusic.R;
import com.example.tomato.oceanmusic.interfaces.SongPlayingOnCallBack;
import com.example.tomato.oceanmusic.models.Album;
import com.example.tomato.oceanmusic.models.Song;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by IceMan on 11/20/2016.
 */

public class SongListPlayingAdapter extends RecyclerView.Adapter<SongListPlayingAdapter.ViewHolderSongPlaying> {

    SongPlayingOnCallBack onCallBack;
    Context mContext;
    ArrayList<Song> mData;
    LayoutInflater mLayoutInflater;

    public SongListPlayingAdapter(Context mContext, ArrayList<Song> mData, SongPlayingOnCallBack onCallBack) {
        this.mContext = mContext;
        this.mData = mData;
        this.onCallBack = onCallBack;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolderSongPlaying onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_song_playing, parent, false);
        ViewHolderSongPlaying holder = new ViewHolderSongPlaying(view);
        return holder;
    }

    public void setFilter(ArrayList<Song> lstFiltered) {
        mData = new ArrayList<>();
        mData.addAll(lstFiltered);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolderSongPlaying holder, int position) {
        if (mData != null && mData.size() > 0) {
            Song item = mData.get(position);
            holder.setId(position);
            String path = mData.get(position).getAlbumImagePath();
            if (path != null) {
                Glide.with(mContext).load(path).into(holder.imgAlbum);
            } else {
                holder.imgAlbum.setImageResource(R.drawable.ic_album_new);
            }
            holder.tvTitle.setText(item.getTitle());
            holder.tvArtist.setText(item.getArtist());
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolderSongPlaying extends RecyclerView.ViewHolder implements View.OnClickListener {
        int id;
        CircleImageView imgAlbum;
        private TextView tvTitle;
        private TextView tvArtist;

        public ViewHolderSongPlaying(View itemView) {
            super(itemView);
            imgAlbum = itemView.findViewById(R.id.img_album_song_play);
            tvTitle = itemView.findViewById(R.id.tv_song_name_play);
            tvArtist = itemView.findViewById(R.id.tv_artist_song_play);
            itemView.setOnClickListener(this);
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public void onClick(View v) {
            onCallBack.onItemClicked(getAdapterPosition(), false);
        }
    }
}
