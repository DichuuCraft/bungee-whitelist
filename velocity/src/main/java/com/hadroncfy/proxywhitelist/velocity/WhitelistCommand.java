package com.hadroncfy.proxywhitelist.velocity;

import java.util.List;

import com.hadroncfy.proxywhitelist.Whitelist;
// 锁尔
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.RawCommand;
import com.velocitypowered.api.command.SimpleCommand;

import org.checkerframework.checker.nullness.qual.NonNull;

public class WhitelistCommand implements RawCommand {
    private Whitelist wl;

    WhitelistCommand(Whitelist l){
        this.wl = l;
    }

    @Override
    public void execute(Invocation invocation) {
        this.wl.getWhitelistCommand().exec(new CommandSenderImpl(invocation.source()), invocation.arguments().split("[ ]+"));
    }

    // @Override
    // public void execute(CommandSource source, String @NonNull [] args) {
    //     wl.getWhitelistCommand().exec(new CommandSenderImpl(source), args);
    // }
    
    // @Override
    // public List<String> suggest(CommandSource source, String @NonNull [] currentArgs) {
    //     return wl.getWhitelistCommand().doCompletion(currentArgs);
    // }
    
    // @Override
    // public boolean hasPermission(CommandSource source, String @NonNull [] args) {
    //     return source.hasPermission("bungeewhitelist.use");
    // }
}