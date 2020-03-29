package com.hadroncfy.proxywhitelist;

import java.util.HashMap;
import java.util.UUID;

public class WhitelistStorage extends HashMap<UUID, Profile> {

    private static final long serialVersionUID = 2333732820769506672L;

    public Profile put(Profile value) {
        return put(value.uuid, value);
    }
}