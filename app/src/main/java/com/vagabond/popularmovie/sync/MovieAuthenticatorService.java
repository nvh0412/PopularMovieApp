package com.vagabond.popularmovie.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by HoaNV on 8/23/16.
 */
public class MovieAuthenticatorService extends Service {

    private MovieAuthenticator movieAuthenticator;

    @Override
    public void onCreate() {
        movieAuthenticator = new MovieAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return movieAuthenticator.getIBinder();
    }
}
