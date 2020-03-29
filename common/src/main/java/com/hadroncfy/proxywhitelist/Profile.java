package com.hadroncfy.proxywhitelist;

import java.util.UUID;

public class Profile {
    public UUID uuid;
    public String name;
    public Profile(UUID uuid, String name){
        this.uuid = uuid;
        this.name = name;
    }

    public static Profile fromResponse(UUIDResponse r){
        return new Profile(r.id, r.name);
    }
}