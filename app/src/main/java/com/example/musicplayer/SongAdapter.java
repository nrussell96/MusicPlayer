package com.example.musicplayer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongHolder> {

    private ArrayList<Audio> songs = new ArrayList<Audio>();
    private Context context;
    private OnItemClickListener mOnItemClickListener;

    public SongAdapter(Context context, ArrayList<Audio> songs) {
        this.context = context;
        this.songs = songs;
    }

    public interface OnItemClickListener {
        void onItemClick(Button b ,View view, Audio obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }


    @Override
    public SongHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View myView = LayoutInflater.from(context).inflate(R.layout.song_page,viewGroup,false);
        return new SongHolder(myView);
    }

    @Override
    public void onBindViewHolder(final SongHolder songHolder, final int i) {
        final Audio song = songs.get(i);
        songHolder.songName.setText(songs.get(i).getTitle());
        songHolder.artist.setText(songs.get(i).getArtist());
        songHolder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(songHolder.play,v, song, i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class SongHolder extends RecyclerView.ViewHolder {
        TextView songName,artist;
        Button play;
        public SongHolder(View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.tvSongName);
            artist = itemView.findViewById(R.id.tvArtistName);
            play = itemView.findViewById(R.id.playPause);
        }
    }
}
