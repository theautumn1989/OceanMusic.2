package com.example.tomato.oceanmusic.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;


import com.example.tomato.oceanmusic.R;
import com.example.tomato.oceanmusic.activities.AlbumListActivity;
import com.example.tomato.oceanmusic.activities.MainActivity;
import com.example.tomato.oceanmusic.activities.PlayingQueenActivity;
import com.example.tomato.oceanmusic.adapter.FragmentAlbumAdapter;
import com.example.tomato.oceanmusic.interfaces.AlbumOnCallBack;
import com.example.tomato.oceanmusic.models.Album;
import com.example.tomato.oceanmusic.services.MusicService;
import com.example.tomato.oceanmusic.utils.DataCenter;

import java.util.ArrayList;

public class FragmentAlbum extends Fragment implements AlbumOnCallBack {

    public static final int COLUMS_RECYCLER = 2;

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
        //  showAlbumList();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showAlbumList();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void initViews() {
        rvAlbumList = view.findViewById(R.id.rv_album_list);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), COLUMS_RECYCLER);
        rvAlbumList.setLayoutManager(layoutManager);
        rvAlbumList.setHasFixedSize(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        RecyclerView.LayoutManager layoutManager;
        switch (item.getItemId()) {
            case R.id.list:
                layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                rvAlbumList.setLayoutManager(layoutManager);
                rvAlbumList.setHasFixedSize(true);
                break;
            case R.id.gird:
                layoutManager = new GridLayoutManager(getActivity(), COLUMS_RECYCLER);
                rvAlbumList.setLayoutManager(layoutManager);
                rvAlbumList.setHasFixedSize(true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_detail, menu);
        MenuItem item = menu.findItem(R.id.action_search_detail);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);


        final EditText searchPlate = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchPlate.setHint(this.getString(R.string.search_toolbar));
        searchPlate.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        searchPlate.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

        ImageView ivClose = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        ivClose.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white));

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlbumList();
                searchPlate.setText("");
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true; //do the default
            }

            @Override
            public boolean onQueryTextChange(String s) {
                albumGridAdapter.setFilter(filter(lstAlbum, s));
                if (s.length() == 0) {
                    showAlbumList();
                }
                return false;
            }

        });
        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                albumGridAdapter.setFilter(lstAlbum);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {

                showAlbumList();
                return true;
            }

        });
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
        setLstAlbum(filteredAlbumList);     // note
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

    public void setLstAlbum(ArrayList<Album> lstAlbum) {
        this.lstAlbum = lstAlbum;
    }

}
