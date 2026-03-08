package gamezone.services;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import gamezone.DataBase;
import gamezone.domain.Formats;
import gamezone.domain.NamedParameterStatement;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CommonServiceImpl extends AbstractService {
    private static final TypeAdapter<Boolean> booleanAsIntAdapter = new TypeAdapter<Boolean>() {
        @Override
        public void write(JsonWriter out, Boolean value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value);
            }
        }

        @Override
        public Boolean read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            return switch (peek) {
                case BOOLEAN -> in.nextBoolean();
                case NULL -> {
                    in.nextNull();
                    yield null;
                }
                case NUMBER -> in.nextInt() != 0;
                case STRING -> Boolean.parseBoolean(in.nextString());
                default -> throw new IllegalStateException("Expected BOOLEAN or NUMBER but was " + peek);
            };
        }
    };
    private static final TypeAdapter<Time> sqlTimeAdapter = new TypeAdapter<>() {
        @Override
        public void write(JsonWriter out, Time value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.value(Formats.getHHMM().format(value));
        }

        @Override
        public Time read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            if (peek == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            if (peek != JsonToken.STRING) {
                throw new JsonSyntaxException("Expected SQL Time as string but was " + peek);
            }

            String raw = in.nextString();
            if (raw == null || raw.isBlank()) {
                return null;
            }

            String trimmed = raw.trim();
            try {
                return Time.valueOf(trimmed.length() == 5 ? trimmed + ":00" : trimmed);
            } catch (IllegalArgumentException ignored) {
                try {
                    LocalTime localTime = LocalTime.parse(trimmed, DateTimeFormatter.ofPattern("H:mm"));
                    return Time.valueOf(localTime);
                } catch (DateTimeParseException e) {
                    throw new JsonSyntaxException("Failed parsing '" + raw + "' as SQL Time", e);
                }
            }
        }
    };
    private Gson gson = null;
    private Gson gsonWithoutNulls = null;
    private Gson gsonPlus = null;

    private CommonServiceImpl() {
    }

    public static CommonServiceImpl getInstance() {
        return new CommonServiceImpl();
    }


    public Gson gson() {
        if (gson == null) {
            gson = new GsonBuilder().setDateFormat(Formats.timestampTimeZoneString)
                    .registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
                    .registerTypeAdapter(boolean.class, booleanAsIntAdapter)
                    .registerTypeAdapter(Time.class, sqlTimeAdapter)
                    .serializeNulls()
                    .disableHtmlEscaping()
                    .create();
        }
        return gson;
    }

    public Gson gson(String format) {
        if (gson == null) {
            gson = new GsonBuilder()
                    .setDateFormat(format)
                    .registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
                    .registerTypeAdapter(boolean.class, booleanAsIntAdapter)
                    .registerTypeAdapter(Double.class, (JsonSerializer<Time>)
                            (src, typeOfSrc, context) -> new JsonPrimitive(Formats.getHHMM().format(src)))
                    .serializeNulls()
                    .disableHtmlEscaping()
                    .create();
        }
        return gson;
    }


    public Gson gsonWithoutNulls() {
        if (gsonWithoutNulls == null) {
            gsonWithoutNulls = new GsonBuilder()
                    .setDateFormat(Formats.timestampTimeZoneString)
                    .registerTypeAdapter(Time.class, sqlTimeAdapter)
                    .registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
                    .registerTypeAdapter(boolean.class, booleanAsIntAdapter)
                    .disableHtmlEscaping()
                    .create();
        }
        return gsonWithoutNulls;
    }

    public Gson gsonWithoutNulls(String dateFormat) {
        if (gsonWithoutNulls == null) {
            gsonWithoutNulls = new GsonBuilder()
                    .setDateFormat(dateFormat)
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
                    .registerTypeAdapter(boolean.class, booleanAsIntAdapter)
                    .create();
        }
        return gsonWithoutNulls;
    }

    public Gson gsonPlus() {
        if (gsonPlus == null) {
            gsonPlus = new GsonBuilder()
                    .setDateFormat(Formats.timestampTimeZoneStringWithPlus)
                    .registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
                    .registerTypeAdapter(boolean.class, booleanAsIntAdapter)
                    .disableHtmlEscaping()
                    .serializeNulls()
                    .create();
        }
        return gsonPlus;
    }
}
