package com.hadroncfy.proxywhitelist.bungeecord;

import com.hadroncfy.proxywhitelist.ICommandSender;
import com.hadroncfy.proxywhitelist.Whitelist;


import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class WhitelistCommand extends Command implements TabExecutor {
    private Whitelist whitelist;

    public WhitelistCommand(Whitelist whitelist) {
        super("bwhitelist", "bungeewhitelist.use");
        this.whitelist = whitelist;
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return whitelist.getWhitelistCommand().doCompletion(args);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        whitelist.getWhitelistCommand().exec(new ICommandSender(){
        
            @Override
            public void sendResultMessage(String msg) {
                sender.sendMessage(new TextComponent(msg));
            }
        
            @Override
            public String getLabel() {
                return sender.getName();
            }
        }, args);
    }

}