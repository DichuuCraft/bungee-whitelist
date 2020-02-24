package com.hadroncfy.proxywhitelist;

import java.util.UUID;

public interface IPlayer extends ICommandSender {
    UUID getUUID();
    String getName();
    void disconnect(String msg);
}