package com.jnovosad.playlist;

import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.model_objects.specification.Track;

import java.util.Random;

public class Artist {
    private String name;
    private String id;
    private Track []topTracks;
    private CountryCode countryCode = CountryCode.CA;
    private Authenticate user;

    Artist(com.wrapper.spotify.model_objects.specification.Artist artist, Authenticate user){
        this.name = artist.getName();
        this.id = artist.getId();
        this.user = user;

        try{
            this.topTracks = user.getSpotifyApi().getArtistsTopTracks(this.id, this.countryCode).build().execute();
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    /*
        Returns a URI of a random top track from the artist
     */
    public String getRandomTopSong() {
        Random random = new Random();
        int randomNumber = random.nextInt(topTracks.length);
        return topTracks[randomNumber].getUri();
    }
}
