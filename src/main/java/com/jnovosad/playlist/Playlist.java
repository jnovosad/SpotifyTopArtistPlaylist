package com.jnovosad.playlist;

//import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.Track;


public class Playlist {
    private Authenticate user;
    private String name;
    private String playlistID;
    private Track[] tracklist;
    private boolean nameAlreadyExists;

    Playlist(String name, Authenticate user) {
        this.user = user;
        this.name = name;
        this.nameAlreadyExists = true; // Set default to true in order to enforce playlist name check
    }

    public String getName() {
        return this.name;
    }
    void setPlaylistID(String id) {this.playlistID = id;}
    void setTracklistSize(int tracklistAmount) {
        this.tracklist = new Track[tracklistAmount];
    }

    boolean checkIfPlaylistNameExists(String nameToCompare) {
        if (this.name.equals(nameToCompare)) {
            this.nameAlreadyExists = true;
            return true;
        }
        this.nameAlreadyExists = false;
        return false;
    }

    String createNewPlaylist(String userID, Authenticate user) {
        if(!this.nameAlreadyExists) {
            try {
                com.wrapper.spotify.model_objects.specification.Playlist playlist = user.getSpotifyApi()
                        .createPlaylist(userID, this.name).public_(false).build().execute();
                System.out.println("Your playlist " + this.name + " has been created.");

                this.playlistID = playlist.getId();
                return playlist.getId();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return e.getMessage();
            }
        }
        else {
            System.out.println("The playlist already exists.");
            return this.playlistID;
        }
    }

    void replaceSongsinSpotify(String playlistID, String[] uris) {
        try {
            String string = user.getSpotifyApi().replacePlaylistsTracks(playlistID, uris).build().execute();
            System.out.println("Null: " + string);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}