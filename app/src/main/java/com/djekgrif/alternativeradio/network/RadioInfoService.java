package com.djekgrif.alternativeradio.network;

import com.djekgrif.alternativeradio.di.modules.ApiModule;
import com.djekgrif.alternativeradio.network.model.SongInfo;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by djek-grif on 5/31/16.
 */
public interface RadioInfoService {

    @Headers(ApiModule.USER_AGENT_DEFAULT)
    @GET
    Observable<SongInfo> getSoundInfo(@Url String url);

    @Headers(ApiModule.USER_AGENT_DEFAULT)
    @GET
    Observable<ResponseBody> getRecentlyList(@Url String url);

}
