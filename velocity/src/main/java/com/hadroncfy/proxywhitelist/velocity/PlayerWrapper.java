package com.hadroncfy.proxywhitelist.velocity;

import java.util.UUID;

import com.hadroncfy.proxywhitelist.IPlayer;
import com.velocitypowered.api.proxy.Player;

import net.kyori.text.TextComponent;

public class PlayerWrapper implements IPlayer {
    private Player p;

    public PlayerWrapper(Player p){
        this.p = p;
    }

    @Override
    public void sendResultMessage(String msg) {
        p.sendMessage(TextComponent.of(msg));
    }

    @Override
    public String getLabel() {
        return p.getUsername();
    }

    @Override
    public UUID getUUID() {
        return p.getUniqueId();
    }

    @Override
    public String getName() {
        return getLabel();
    }

    @Override
    public void disconnect(String msg) {
        p.disconnect(TextComponent.of(msg));
    }
    
}