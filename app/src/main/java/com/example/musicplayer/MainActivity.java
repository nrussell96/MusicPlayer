package com.example.musicplayer;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String Broadcast_PLAY_NEW_AUDIO = "com.example.musicplayer.PlayNewAudio";

    private MusicPlayerService mp;
    private boolean serviceBound = false;
    ArrayList<Audio> songs;

    private void initRecyclerView() {
        if (songs.size() > 0) {
            RecyclerView recyclerView = findViewById(R.id.recyclerview);
            SongAdapter adapter = new SongAdapter(songs, getApplication());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addOnItemTouchListener(new TouchListener(this, new onItemClickListener() {
                @Override
                public void onItemClick(View view, int i) {
                    playSong(i);
                }
            }));

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkPermission();
        initRecyclerView();
    }

    // Permissions reference: https://developer.android.com/guide/topics/permissions/requesting.html
    //method to request permission to use device storage
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
            songs = new ArrayList<>();
            while (cursor.moveToNext()){
                String songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                //Audio song = new Audio(songName, artist, album, data);
                songs.add(new Audio(songName, artist, album, data));
            }
            cursor.close();
        }
    }


        private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {


            MusicPlayerService.LocalBinder binder = (MusicPlayerService.LocalBinder) service;
            mp = binder.getService();
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

    private void playSong(int songIndex){

        if(!serviceBound){
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudio(songs);
            storage.storeAudioIndex(songIndex);

            Intent playerIntent = new Intent(this, MusicPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }else{
            //service active
            //send media/w broadcast receiver
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudioIndex(songIndex);

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }
    }

    @Override
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
            mp.stopSelf();
        }
    }

}

