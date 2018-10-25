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
import com.example.tomato.oceanmusic.activities.ArtistListActivity;
import com.example.tomato.oceanmusic.adapter.FragmentArtistAdapter;
import com.example.tomato.oceanmusic.interfaces.ArtistOnCallBack;
import com.example.tomato.oceanmusic.models.Artist;
import com.example.tomato.oceanmusic.services.MusicService;
import com.example.tomato.oceanmusic.utils.DataCenter;

import java.util.ArrayList;

public class FragmentArtist extends Fragment implements ArtistOnCallBack {

    public static final int COLUMS_RECYCLER = 2;
    View view;
    RecyclerView rvListArtist;
    ArrayList<Artist> listArtist;
    FragmentArtistAdapter artistAdapter;
    MusicService mService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_artist, container, false);
        initViews();
        showListArtist();
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
        final EditText searchPlate = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchPlate.setHint(getString(R.string.search_toolbar));
        searchPlate.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        searchPlate.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

        ImageView ivClose = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        ivClose.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white));

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showListArtist();
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
                artistAdapter.filter(filter(listArtist, s));
                if (s.length() == 0) {
                    showListArtist();
                }
                return false;
            }

        });
        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                artistAdapter.filter(listArtist);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {

                showListArtist();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        RecyclerView.LayoutManager layoutManager;
        switch (item.getItemId()) {
            case R.id.list:
                layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                rvListArtist.setLayoutManager(layoutManager);
                rvListArtist.setHasFixedSize(true);
                break;
            case R.id.gird:
                layoutManager = new GridLayoutManager(getActivity(), COLUMS_RECYCLER);
                rvListArtist.setLayoutManager(layoutManager);
                rvListArtist.setHasFixedSize(true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<Artist> filter(ArrayList<Artist> lstArtist, String query) {
        query = query.toLowerCase();
        ArrayList<Artist> filteredArtistList = new ArrayList<>();
        for (Artist artist : lstArtist) {
            String text = artist.getName().toLowerCase();
            if (text.contains(query)) {
                filteredArtistList.add(artist);
            }
        }
        return filteredArtistList;
    }

    private void showListArtist() {
        listArtist = DataCenter.instance.getListArtist();
        artistAdapter = new FragmentArtistAdapter(getActivity(), listArtist, this);
        rvListArtist.setAdapter(artistAdapter);
    }


    private void initViews() {
        rvListArtist = view.findViewById(R.id.rv_artist_list);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        rvListArtist.setLayoutManager(layoutManager);
    }

    @Override
    public void onItemClicked(int position, boolean isLongClick) {
        Intent intent = new Intent(getActivity(), ArtistListActivity.class);
        mService = (MusicService) DataCenter.instance.musicService;
        mService.setIDArtist(listArtist.get(position).getId());
        getActivity().startActivity(intent);
    }
}
