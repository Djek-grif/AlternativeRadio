package com.djekgrif.alternativeradio.di.modules;

import com.djekgrif.alternativeradio.BuildConfig;
import com.djekgrif.alternativeradio.common.Logger;
import com.djekgrif.alternativeradio.network.NetworkInterceptor;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by djek-grif on 5/26/16.
 */
@Module
public class NetworkModule {

    @Singleton
    @Provides
    OkHttpClient provideOkHttpClient(){
        X509TrustManager x509TrustManager = buildX509TrustManager();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if(BuildConfig.DEBUG) {
            NetworkInterceptor logging = new NetworkInterceptor();
            builder.addInterceptor(logging);
        }
        builder.connectTimeout(30, SECONDS);
        builder.readTimeout(30, SECONDS);
        builder.writeTimeout(30, SECONDS);
        builder.sslSocketFactory(buildSSLSocketFactory(x509TrustManager), x509TrustManager);
        return builder.build();
    }


    private SSLSocketFactory buildSSLSocketFactory(X509TrustManager x509TrustManager){
        try {
            // Construct SSLSocketFactory that accepts any cert.
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{x509TrustManager}, null);
            return context.getSocketFactory();
        } catch (Exception e) {
            Logger.e("Error provide SSLSocketFactory");
            throw new AssertionError(e);
        }
    }


    private X509TrustManager buildX509TrustManager(){
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }


}
