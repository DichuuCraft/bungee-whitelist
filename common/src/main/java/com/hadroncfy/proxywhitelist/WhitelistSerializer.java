package com.hadroncfy.proxywhitelist;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class WhitelistSerializer implements JsonSerializer<WhitelistStorage>, JsonDeserializer<WhitelistStorage> {

    private static final WhitelistSerializer instance = new WhitelistSerializer();

    public static WhitelistSerializer getInstance(){
        return instance;
    }

    @Override
    public WhitelistStorage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (json.isJsonArray()){
            final WhitelistStorage ret = new WhitelistStorage();
            for (JsonElement e: json.getAsJsonArray()){
                Profile p = context.deserialize(e, Profile.class);
                ret.put(p);
            }
            return ret;
        }
        throw new JsonParseException("JSON array expected");
    }

    @Override
    public JsonElement serialize(WhitelistStorage src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray ret = new JsonArray();
        src.forEach((uuid, p) -> ret.add(context.serialize(p)));
        return ret;
    }
    
}