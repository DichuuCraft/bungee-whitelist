package com.hadroncfy.bungeewhitelist;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.UUID;

public class UUIDTypeAdapter extends TypeAdapter<UUID> {
    public void write(JsonWriter w, UUID uuid) throws IOException {
        w.value(uuid.toString());
    }

    public UUID read(JsonReader r) throws IOException {
        return UUID.fromString(r.nextString());
    }
}