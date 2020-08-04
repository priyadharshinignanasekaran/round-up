package tech.test.deserialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.ZonedDateTime;

public class GsonFactory {
    public static Gson create() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeDeserializer());
        return gsonBuilder.create();
    }

    private GsonFactory() {
    }
}
