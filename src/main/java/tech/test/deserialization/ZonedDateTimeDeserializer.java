package tech.test.deserialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeDeserializer implements JsonDeserializer<ZonedDateTime>, JsonSerializer<ZonedDateTime> {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSz");

    @Override
    public ZonedDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        return ZonedDateTime.parse(jsonElement.getAsString(), formatter);
    }

    @Override
    public JsonElement serialize(ZonedDateTime ZonedDateTime, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(ZonedDateTime.format(formatter));
    }
}
