package com.example.tomato.oceanmusic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.tomato.oceanmusic.R;
import com.example.tomato.oceanmusic.interfaces.ArtistOnCallBack;
import com.example.tomato.oceanmusic.models.Artist;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by IceMan on 11/12/2016.
 */

public class FragmentArtistAdapter extends RecyclerView.Adapter<FragmentArtistAdapter.ViewHolderArtist> {


    ArtistOnCallBack onCallBack;
    Context mContext;
    ArrayList<Artist> mData;
    LayoutInflater mLayoutInflater;

    public FragmentArtistAdapter(Context mContext, ArrayList<Artist> mData, ArtistOnCallBack onCallBack) {
        this.mContext = mContext;
        this.mData = mData;
        this.onCallBack = onCallBack;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolderArtist onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_artist, null);
        ViewHolderArtist holder = new ViewHolderArtist(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolderArtist holder, int position) {
        if (mData != null && mData.size() > 0) {
            holder.tvArtist.setText(mData.get(position).getName());
            holder.tvNumberSong.setText(mData.get(position).getNumberAlbum() + " album | " + mData.get(position).getNumberSong() + " song");
            holder.setId(position);
        }
    }

    public void filter(ArrayList<Artist> lstArtist) {
        mData = new ArrayList<>();
        mData.addAll(lstArtist);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolderArtist extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvArtist;
        int id;
        TextView tvNumberSong;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public ViewHolderArtist(View itemView) {
            super(itemView);
            tvArtist = itemView.findViewById(R.id.artist_title_item);
            tvNumberSong = itemView.findViewById(R.id.tv_artist_number_song);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onCallBack.onItemClicked(getAdapterPosition(), false);
        }
    }
}
