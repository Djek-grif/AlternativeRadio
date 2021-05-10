package com.djekgrif.alternativeradio.network;

import android.app.Application;

/**
 * Created by djek-grif on 7/2/17.
 */

public class ApiServiceImp extends ApiServiceBase {

    public ApiServiceImp(RadioInfoService radioInfoService, SongInfoService songInfoService) {
        super(radioInfoService, songInfoService);
    }
}
