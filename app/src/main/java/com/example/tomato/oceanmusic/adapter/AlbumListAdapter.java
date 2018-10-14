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
import com.example.tomato.oceanmusic.interfaces.AlbumOnCallBack;
import com.example.tomato.oceanmusic.models.Album;


import java.util.ArrayList;

/**
 * Created by iceman on 10/11/2016.
 */

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.ViewHolderAlbum> {

    public static final String ALBUM_KEY = "album_key";

    AlbumOnCallBack onCallBack;

    Context mContext;
    ArrayList<Album> mData;
    LayoutInflater mLayoutInflater;

    public AlbumListAdapter(Context mContext, ArrayList<Album> mData, AlbumOnCallBack onCallBack) {
        this.mContext = mContext;
        this.mData = mData;
        this.onCallBack = onCallBack;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolderAlbum onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_album, null);
        ViewHolderAlbum holder = new ViewHolderAlbum(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolderAlbum holder, int position) {
        Album item = mData.get(position);
        String path = mData.get(position).getAlbumArtPath();
        if (path != null) {
            Glide.with(mContext).load(path).into(holder.ivImgAlbum);
        } else {
            holder.ivImgAlbum.setImageResource(R.drawable.default_cover_big);
        }
        holder.tvAlbumTitle.setText(item.getTitle());
        holder.tvArtist.setText(item.getArtist());
        holder.setId(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setFilter(ArrayList<Album> lstFiltered) {
        mData = new ArrayList<>();
        mData.addAll(lstFiltered);
        notifyDataSetChanged();
    }

    public class ViewHolderAlbum extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivImgAlbum;
        TextView tvAlbumTitle;
        TextView tvArtist;
        int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public ViewHolderAlbum(View itemView) {
            super(itemView);
            ivImgAlbum = itemView.findViewById(R.id.iv_album_img_item);
            tvAlbumTitle = itemView.findViewById(R.id.tv_album_title_item);
            tvArtist = itemView.findViewById(R.id.tv_artist_album_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onCallBack.onItemClicked(getAdapterPosition(), false);
        }
    }
}
