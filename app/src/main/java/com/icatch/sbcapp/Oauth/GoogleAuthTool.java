package com.icatch.sbcapp.Oauth;

import android.content.Context;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.icatch.sbcapp.Log.AppLog;
import com.icatch.sbcapp.R;

import java.io.IOException;

/**
 * Created by b.jiang on 2017/4/18.
 */

public class GoogleAuthTool {
    private static String TAG = "GoogleAuthTool";


    public static String refreshAccessToken(Context context,String refreshToken) throws IOException {
        String clientId = context.getString(R.string.server_client_id);
        String clientSecret = context.getString(R.string.server_client_secret);
        AppLog.d(TAG,"start refreshAccessToken refreshToken=" + refreshToken);
        String accessToken = null;
        try {
            TokenResponse response =
                    new GoogleRefreshTokenRequest(new NetHttpTransport(), new JacksonFactory(),
                            refreshToken, clientId, clientSecret).execute();
            accessToken =  response.getAccessToken();
        } catch (TokenResponseException e) {
            accessToken =  null;
        }
        AppLog.d(TAG,"Ens refreshAccessToken accessToken=" + accessToken);
        return accessToken;
    }
}
