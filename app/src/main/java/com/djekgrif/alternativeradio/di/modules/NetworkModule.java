package com.djekgrif.alternativeradio.di.modules;

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
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

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
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .addInterceptor(logging)
                .connectTimeout(30, SECONDS)
                .readTimeout(30, SECONDS)
                .writeTimeout(30, SECONDS)
                .sslSocketFactory(buildSSLSocketFactory(x509TrustManager), x509TrustManager)
                .build();
        return okHttpClient;
    }


    private SSLSocketFactory buildSSLSocketFactory(X509TrustManager x509TrustManager){
        try {
            // Construct SSLSocketFactory that accepts any cert.
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{x509TrustManager}, null);
            return context.getSocketFactory();
        } catch (Exception e) {
            Timber.e("Error provide SSLSocketFactory");
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
