package com.example.musicplayer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongHolder> {

    private List<Audio> songs = Collections.emptyList();
    private Context context;

    public SongAdapter(List<Audio> songs, Context context) {
        this.context = context;
        this.songs = songs;
    }

    @Override
    public SongHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View myView = LayoutInflater.from(context).inflate(R.layout.item_layout, viewGroup, false);
        return new SongHolder(myView);
    }

    @Override
    public void onBindViewHolder(SongHolder songHolder, int i) {
    //    final Audio song = songs.get(i);
        songHolder.songName.setText(songs.get(i).getTitle());
    //    songHolder.artist.setText(songs.get(i).getArtist());
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
    class SongHolder extends RecyclerView.ViewHolder {
        TextView songName;
        ImageView play;
        SongHolder(View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.songTitle);
            //artist = itemView.findViewById(R.id.tvArtistName);
            play = itemView.findViewById(R.id.playPause);
        }
    }

