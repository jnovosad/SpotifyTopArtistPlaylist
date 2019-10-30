package com.jnovosad.playlist;

import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.Track;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args){
        Authenticate spotifyUser = new Authenticate();
        spotifyUser.authorizationCode();

        try {
            String userID = spotifyUser.getSpotifyApi().getCurrentUsersProfile().build().execute().getId();

            // Check that playlist names don't already exist in a user's list

            Paging<PlaylistSimplified> userPlaylists = spotifyUser.getSpotifyApi().getListOfCurrentUsersPlaylists()
                    .limit(50).build().execute();

            PlaylistSimplified []listOfPlaylists = userPlaylists.getItems();

            Playlist myTopArtistPlaylist = new Playlist("A mix of your faves", spotifyUser);

            // TODO need to check for offset (50 playlist max limit currently, will need for loop)

            for(PlaylistSimplified name: listOfPlaylists){
                if(myTopArtistPlaylist.checkIfPlaylistNameExists(name.getName())){
                    myTopArtistPlaylist.setPlaylistID(name.getId());
                    break;
                }
            }

            // Generate playlist or update existing (popularity < 20), return playlist ID:

            String playlistID = myTopArtistPlaylist.createNewPlaylist(userID, spotifyUser);

            // Find current user's top artists -> TODO get the related artists (with limit on popularity)

            final int PLAYLIST_SIZE = 40;
            myTopArtistPlaylist.setTracklistSize(PLAYLIST_SIZE);

            Paging<Artist> userTopArtists = spotifyUser.getSpotifyApi().getUsersTopArtists()
                    .limit(PLAYLIST_SIZE).time_range("short_term").build().execute();

            Artist []userTopArtistsList = userTopArtists.getItems();

            // For every artist, get their top tracks (as part of Artist class)
            List<com.jnovosad.playlist.Artist> playlistArtists = new ArrayList<com.jnovosad.playlist.Artist>();

            for(Artist artist: userTopArtistsList) {
                playlistArtists.add(new com.jnovosad.playlist.Artist(artist, spotifyUser));

            }

            String uris[] = new String[playlistArtists.size()];

            // Pick one random track from each artist
            for(int i = 0; i < playlistArtists.size(); i++) {
                uris[i] = playlistArtists.get(i).getRandomTopSong();
            }

            // Add tracks to playlist
            myTopArtistPlaylist.replaceSongsinSpotify(playlistID, uris);

            /*for(com.jnovosad.playlist.Artist artist: playlistArtists) {
                System.out.println(artist.getRandomTopSong());
            }*/

            // Initial test: get random song from every top artist, add to playlist

        }catch(Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
