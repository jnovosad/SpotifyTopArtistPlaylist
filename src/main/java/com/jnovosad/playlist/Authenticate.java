package com.jnovosad.playlist;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.net.URI;
import java.net.URL;
import java.util.Scanner;

public class Authenticate {

    /* Use Authorization Code Flow since requests are bound to a specific user
    https://developer.spotify.com/documentation/general/guides/authorization-guide/
    */

    private static final String CLIENT_ID = "CLIENT_ID";
    private static final String CLIENT_SECRET = "CLIENT_SECRET";
    private static final URI REDIRECT_URI = SpotifyHttpManager.makeUri("https://www.google.com/");
    private static URL redirectURL;
    private static String code;

    private static final SpotifyApi SPOTIFY_API = new SpotifyApi.Builder()
            .setClientId(CLIENT_ID)
            .setClientSecret(CLIENT_SECRET)
            .setRedirectUri(REDIRECT_URI)
            .build();

    private static final AuthorizationCodeUriRequest authorizationCodeUriRequest = SPOTIFY_API.authorizationCodeUri()
            .scope("user-read-recently-played, user-top-read, playlist-read-private, " +
                    "playlist-modify-private, user-read-email")
            .show_dialog(true)
            .build();

    /*
    public static void authorizationCodeUri(){
        final URI uri = authorizationCodeUriRequest.execute();

        System.out.println("URI: " + uri.toString());
    }
    */

    static {
        final URI uri = authorizationCodeUriRequest.execute();

        System.out.println("URI: " + uri.toString());

        System.out.println("Open the link above and paste the redirect link below:" + "\n" +
                "Note that you need to add an extra space after pasting the URL before clicking Enter.");

        /*Alternatively, could use Buffered reader below:*/
        try {
            Scanner in = new Scanner(System.in);
            String input = in.nextLine();
            redirectURL = new URL(input);
            code = redirectURL.getQuery().split("code=")[1];
        } catch(Exception e){
            System.out.println("Not a valid link.");
        }
    }

    private static final AuthorizationCodeRequest authorizationCodeRequest = SPOTIFY_API.authorizationCode(code)
            .build();

    //  Class methods:

    public void authorizationCode(){
        try{
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            // Set access & refresh tokens:
            SPOTIFY_API.setAccessToken(authorizationCodeCredentials.getAccessToken());
            SPOTIFY_API.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
        } catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }


    /*  TODO: Incorporate how to use refresh tokens
    */

    public SpotifyApi getSpotifyApi(){
        return SPOTIFY_API;
    }

}
