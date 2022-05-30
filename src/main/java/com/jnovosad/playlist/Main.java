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

            PlaylistSimplified[] listOfPlaylists = userPlaylists.getItems();

            Playlist myTopArtistPlaylist = new Playlist("A mix of your faves", spotifyUser);
            Playlist relatedArtistPlaylist = new Playlist("A mix of future faves", spotifyUser);
            final int PLAYLIST_SIZE = 40;

            // TODO need to check for offset (50 playlist max limit currently, will need for loop)
            // TODO duplicated code, make into function?
            for(PlaylistSimplified name: listOfPlaylists){
                if(myTopArtistPlaylist.checkIfPlaylistNameExists(name.getName())){
                    myTopArtistPlaylist.setPlaylistID(name.getId());
                    break;
                }
            }

            for(PlaylistSimplified name: listOfPlaylists){
                if(relatedArtistPlaylist.checkIfPlaylistNameExists(name.getName())){
                    relatedArtistPlaylist.setPlaylistID(name.getId());
                    break;
                }
            }

            // Generate playlist or update existing, return playlist ID:

            String topPlaylistID = myTopArtistPlaylist.createNewPlaylist(userID, spotifyUser);
            String relatedPlaylistID = relatedArtistPlaylist.createNewPlaylist(userID, spotifyUser);
            myTopArtistPlaylist.setTracklistSize(PLAYLIST_SIZE);
            relatedArtistPlaylist.setTracklistSize(PLAYLIST_SIZE);

            // Find current user's top artists -> TODO get the related artists

            Paging<Artist> userTopArtists = spotifyUser.getSpotifyApi().getUsersTopArtists()
                    .limit(PLAYLIST_SIZE).time_range("short_term").build().execute();

            Artist []userTopArtistsList = userTopArtists.getItems();

            // For every artist, get their top tracks (as part of Artist class)
            List<com.jnovosad.playlist.Artist> topPlaylistArtists = new ArrayList<com.jnovosad.playlist.Artist>();

            for(Artist artist: userTopArtistsList) {
                topPlaylistArtists.add(new com.jnovosad.playlist.Artist(artist, spotifyUser));
            }

            String[] topArtistUris = new String[topPlaylistArtists.size()];

            // Pick one random track from each artist
            for(int i = 0; i < topPlaylistArtists.size(); i++) {
                topArtistUris[i] = topPlaylistArtists.get(i).getRandomTopSong();
            }

            // Add tracks to playlist
            myTopArtistPlaylist.replaceSongsinSpotify(topPlaylistID, topArtistUris);


            /*
            *       The below is for the related artists playlist creation
            *
            * */

            List<com.jnovosad.playlist.Artist> relatedPlaylistArtists = new ArrayList<com.jnovosad.playlist.Artist>();

            for(com.jnovosad.playlist.Artist artist: topPlaylistArtists){
                artist.setRelatedArtists();
                relatedPlaylistArtists.add(
                        new com.jnovosad.playlist.Artist(artist.getRandomRelatedArtist(), spotifyUser));
            }

            String[] relatedArtistUris = new String[relatedPlaylistArtists.size()];

            for(int i = 0; i < relatedPlaylistArtists.size(); i++) {
                relatedArtistUris[i] = relatedPlaylistArtists.get(i).getRandomTopSong();
            }

            relatedArtistPlaylist.replaceSongsinSpotify(relatedPlaylistID, relatedArtistUris);

            // Initial test: get random song from every top artist, add to playlist

        }catch(Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
