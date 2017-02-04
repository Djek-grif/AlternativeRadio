package com.djekgrif.alternativeradio.network.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

/**
 * Created by djek-grif on 5/31/16.
 */
public class DateTimeTypeAdapter extends TypeAdapter<DateTime> {
    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @Override
    public void write(JsonWriter out, DateTime value) throws IOException {
        out.value(value != null ? DATE_TIME_FORMAT.print(value) : null);
    }

    @Override
    public DateTime read(JsonReader in) throws IOException {
        return DATE_TIME_FORMAT.withOffsetParsed().parseDateTime(in.nextString());
    }
}
