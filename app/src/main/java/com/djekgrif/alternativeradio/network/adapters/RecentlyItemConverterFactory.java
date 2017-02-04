package com.djekgrif.alternativeradio.network.adapters;

import com.djekgrif.alternativeradio.network.model.RecentlyItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by djek-grif on 1/8/17.
 */

public class RecentlyItemConverterFactory extends Converter.Factory{

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (String.class.equals(type)) {
            return new Converter<ResponseBody, List<RecentlyItem>>() {
                @Override
                public List<RecentlyItem> convert(ResponseBody value) throws IOException {
                    return fromStream(value.byteStream());
                }
            };
        }
        return null;
    }

    public static List<RecentlyItem> fromStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        List<RecentlyItem> recentlyItems = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.replace("<li>", "").replace("</li>", "");
            int indexFirstSpace = line.indexOf(" ");
            String time = line.substring(0, indexFirstSpace);
            line = line.substring(indexFirstSpace, line.length());
            int indexDash = line.indexOf("-");
            String name = line.substring(0, indexDash);
            String track = line.substring(indexDash, line.length());
            recentlyItems.add(new RecentlyItem(name, track, time));
        }
        return recentlyItems;
    }
}
