package com.example.tomato.oceanmusic.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.tomato.oceanmusic.R;
import com.example.tomato.oceanmusic.interfaces.SongOnCallBack;
import com.example.tomato.oceanmusic.models.Song;

import java.util.ArrayList;

/**
 * Created by IceMan on 11/8/2016.
 */

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolderSong> {

    Activity mContext;
    ArrayList<Song> mData;
    LayoutInflater mLayoutInflater;
    SongOnCallBack onCallBack;

    public SongListAdapter(Activity mContext, ArrayList<Song> mData, SongOnCallBack onCallBack) {
        this.mContext = mContext;
        this.mData = mData;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.onCallBack = onCallBack;
    }

    @Override
    public ViewHolderSong onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_song, parent, false);
        ViewHolderSong holder = new ViewHolderSong(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolderSong holder, int position) {
        if(mData != null && mData.size() > 0){
            holder.tvTitle.setText(mData.get(position).getTitle());
            holder.tvArtist.setText(mData.get(position).getArtist());
            holder.setId(position);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setFilter(ArrayList<Song> lstSong) {
        mData = new ArrayList<>();
        mData.addAll(lstSong);
        notifyDataSetChanged();
    }

    public class ViewHolderSong extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvArtist;
        int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public ViewHolderSong(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_song_title_item);
            tvArtist = itemView.findViewById(R.id.artist_name_song_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onCallBack.onItemClicked(getAdapterPosition(), false);
        }
    }
}
