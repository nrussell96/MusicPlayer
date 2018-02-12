package com.example.musicplayer;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mp;
    private RecyclerView recyclerView;
    SeekBar seekBar;
    SongAdapter songAdapter;
//    AudioAdapter audioAdapter;
//    private boolean serviceBound = false;
    private ArrayList<Audio> songs = new ArrayList<Audio>();
    private Handler myHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        songAdapter = new SongAdapter(this,songs);
        recyclerView.setAdapter(songAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        songAdapter.setOnItemClickListener(new SongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final Button b, View view, final Audio obj, int position) {
                if(b.getText().equals("Stop")){
                    mp.stop();
                    mp.reset();
                    mp.release();
                    mp = null;
                    b.setText("Play");
                }else {

                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mp = new MediaPlayer();
                                mp.setDataSource(obj.getSongURL());
                                mp.prepareAsync();
                                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mp.start();
                                        seekBar.setProgress(0);
                                        seekBar.setMax(mp.getDuration());
                                        Log.d("Prog", "run: " + mp.getDuration());
                                    }
                                });
                                b.setText("Stop");



                            }catch (Exception e){}
                        }

                    };
                    myHandler.postDelayed(runnable,100);

                }
            }
        });
        checkPermission();

        Thread t = new runThread();
        t.start();
    }

    public class runThread extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("Thread", "run: " + 1);
                if (mp != null) {
                    seekBar.post(new Runnable() {
                        @Override
                        public void run() {
                            seekBar.setProgress(mp.getCurrentPosition());
                        }
                    });

                    Log.d("Thread", "run: " + mp.getCurrentPosition());
                }
            }
        }
    }

    /* Permissions reference: https://developer.android.com/guide/topics/permissions/requesting.html */

    private void checkPermission() {
        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},123);
                return;
            }
        }
            loadAudio();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
     @NonNull int[] grantResults) {
        switch (requestCode) {

            case 123:
                if ((grantResults.length > 0) && (grantResults[0] ==
                PackageManager.PERMISSION_GRANTED)) {
                    checkPermission();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void loadAudio(){
        //reads contents of device memory, taking values of song name, artist name, and url
        ContentResolver contentResolver = getContentResolver();
        //store contents of URI for song
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        //sort audio contents in ascending order
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        //cursor to provide r/w access to query
        //https://developer.android.com/reference/android/database/Cursor.html
        Cursor cursor = contentResolver.query(uri, null, selection, null,
                sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            if(cursor.moveToFirst()){
                do{
                    String songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String songURL = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                    Audio song = new Audio(songName,artist,songURL);
                    songs.add(song);

                }while (cursor.moveToNext());
            }
            cursor.close();
            songAdapter = new SongAdapter(MainActivity.this,songs);
        }
    }

    /*    Old methods I may reuse as I implement more features

        private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {


            SongAdapter.LocalBinder binder = (SongAdapter.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

            //shows that the player service was bounded in-app
            Toast.makeText(MainActivity.this, "Service Bound",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serviceBound = false;
        }
    };

    private void playSong(String song){

        if(!serviceBound){
            Intent playerIntent = new Intent(this, SongAdapter.class);
            playerIntent.putExtra("song", song);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }else{
            //service active
            //send media/w broadcast receiver
        }
    } */

/*    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            player.stopSelf();
        }
    } */

}
