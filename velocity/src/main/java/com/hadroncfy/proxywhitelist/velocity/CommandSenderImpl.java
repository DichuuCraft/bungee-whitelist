package com.hadroncfy.proxywhitelist.velocity;

import com.hadroncfy.proxywhitelist.ICommandSender;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;

import net.kyori.text.TextComponent;

public class CommandSenderImpl implements ICommandSender {
    private CommandSource cs;

    public CommandSenderImpl(CommandSource cs){
        this.cs = cs;
    }
    @Override
    public void sendResultMessage(String msg) {
        cs.sendMessage(TextComponent.of(msg));
    }

    @Override
    public String getLabel() {
        if (cs instanceof Player){
            return ((Player)cs).getUsername();
        }
        else if (cs instanceof ConsoleCommandSource){
            return "CONSOLE";
        }
        else {
            return "@"; // Even reachable??
        }
    }
    
}