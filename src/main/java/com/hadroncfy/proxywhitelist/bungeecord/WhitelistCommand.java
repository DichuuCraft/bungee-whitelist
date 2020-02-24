package com.hadroncfy.proxywhitelist.bungeecord;

import java.util.ArrayList;
import java.util.List;

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

    private static List<String> filterStart(List<String> l, String prefix){
        List<String> ret = new ArrayList<>();
        for (String s: l){
            if (s.startsWith(prefix)){
                ret.add(s);
            }
        }
        return ret;
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> ret = new ArrayList<>();
        if (args.length == 0 || args.length == 1){
            ret.add("on");
            ret.add("off");
            ret.add("list");
            ret.add("reload");
            ret.add("remove");
            ret.add("add"); 
            if (args.length == 1){
                ret = filterStart(ret, args[0]);
            }
        }
        else if (args.length == 2){
            if (args[1].equals("remove")){
                ret = filterStart(whitelist.list(), args[1]);
            }
        }

        return ret;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        whitelist.execCommand(new ICommandSender(){
        
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