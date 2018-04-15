package com.example.musicplayer;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AudioTest {

    private Audio audio;

    @Before
    public void setUp(){
        audio = new Audio();
    }

    @Test
    public void setData() {
        audio.setData("abcdefg");
        assertEquals("abcdefg", audio.getData());
    }

    @Test
    public void setTitle() {
        audio.setTitle("Some Song");
        assertEquals("Some Song", audio.getTitle());
    }

    @Test
    public void setAlbum() {
        audio.setAlbum("Some Album");
        assertEquals("Some Album", audio.getAlbum());
    }

    @Test
    public void setArtist() {
        audio.setArtist("Some Artist");
        assertEquals("Some Artist", audio.getArtist());
    }
}