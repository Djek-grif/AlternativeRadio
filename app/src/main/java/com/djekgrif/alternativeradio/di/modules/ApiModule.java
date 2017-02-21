package com.djekgrif.alternativeradio.di.modules;

import com.djekgrif.alternativeradio.network.ApiService;
import com.djekgrif.alternativeradio.network.ApiServiceImp;
import com.djekgrif.alternativeradio.network.RadioInfoService;
import com.djekgrif.alternativeradio.network.SongInfoService;
import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by djek-grif on 5/27/16.
 */
@Module (includes = NetworkModule.class)
public class ApiModule {

    public static final String USER_AGENT_DEFAULT = "User-Agent: Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.76 Safari/537.36";

    @Provides
    @Singleton
    Retrofit provideRestAdapter(OkHttpClient client, Gson gson) {
        return new Retrofit.Builder()
                .client(client)
                .baseUrl(ApiServiceImp.RADIO_PLAYER_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    RadioInfoService provideRadioInfoService(Retrofit retrofit) {
        return retrofit.create(RadioInfoService.class);
    }

    @Provides
    @Singleton
    SongInfoService provideSongInfoService(Retrofit retrofit){
        return retrofit.create(SongInfoService.class);
    }

    @Singleton
    @Provides
    ApiService provideApiService(RadioInfoService radioInfoService, SongInfoService songInfoService){
        return new ApiServiceImp(radioInfoService, songInfoService);
    }
}
