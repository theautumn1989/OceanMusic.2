package com.example.tomato.oceanmusic.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.example.tomato.oceanmusic.R;
import com.example.tomato.oceanmusic.activities.AlbumListActivity;
import com.example.tomato.oceanmusic.adapter.FragmentAlbumAdapter;
import com.example.tomato.oceanmusic.interfaces.AlbumOnCallBack;
import com.example.tomato.oceanmusic.models.Album;
import com.example.tomato.oceanmusic.services.MusicService;
import com.example.tomato.oceanmusic.utils.DataCenter;

import java.util.ArrayList;

public class FragmentAlbum extends Fragment implements SearchView.OnQueryTextListener, AlbumOnCallBack {

    MusicService mService;
    View view;
    RecyclerView rvAlbumList;
    FragmentAlbumAdapter albumGridAdapter;
    ArrayList<Album> lstAlbum;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_album, container, false);
        initViews();
        showAlbumList();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_detail, menu);
        MenuItem item = menu.findItem(R.id.action_search_detail);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                albumGridAdapter.setFilter(lstAlbum);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });
    }

    private void initViews() {
        rvAlbumList = view.findViewById(R.id.rv_album_list);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        mRvAlbumList.setLayoutManager(layoutManager);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        rvAlbumList.setLayoutManager(layoutManager);
        rvAlbumList.setHasFixedSize(true);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        albumGridAdapter.setFilter(filter(lstAlbum, newText));
        return true;
    }

    private ArrayList<Album> filter(ArrayList<Album> lstAlbum, String query) {
        query = query.toLowerCase();
        ArrayList<Album> filteredAlbumList = new ArrayList<>();

        for (Album album : lstAlbum) {
            String text = album.getTitle().toLowerCase();
            if (text.contains(query)) {
                filteredAlbumList.add(album);
            }
        }
        return filteredAlbumList;
    }

    @Override
    public void onItemClicked(int position, boolean isLongClick) {

        Intent intent = new Intent(getActivity(), AlbumListActivity.class);
        mService = (MusicService) DataCenter.instance.musicService;
        mService.setIDAlbum(lstAlbum.get(position).getId());
        getActivity().startActivity(intent);
    }

    private void showAlbumList() {

        lstAlbum = DataCenter.instance.getListAlbum();
        albumGridAdapter = new FragmentAlbumAdapter(getActivity(), lstAlbum, this);
        rvAlbumList.setAdapter(albumGridAdapter);

    }

}
