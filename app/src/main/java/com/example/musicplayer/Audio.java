package com.example.musicplayer;

/**
 * Class for storing and accessing song/album/artist info
 **/
public class Audio{

    private String title;
    private String data;
    private String album;
    private String artist;

    public Audio(){

    }

    public Audio(String title, String artist, String album, String data){
        this.data = data;
        this.title = title;
        this.album = album;
        this.artist = artist;
    }

    public String getData(){
        return data;
    }

    public void setData(String data){
        this.data = data;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

   public String getAlbum() {
       return album;
   }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
