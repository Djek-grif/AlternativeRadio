package com.djekgrif.alternativeradio.network;

import com.djekgrif.alternativeradio.di.modules.ApiModule;
import com.djekgrif.alternativeradio.network.model.SongInfoList;

import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by djek-grif on 7/19/16.
 */
public interface SongInfoService {

    @Headers(ApiModule.USER_AGENT_DEFAULT)
    @GET()
    Observable<SongInfoList> getSongDetailsInfo(@Url String url);
}
