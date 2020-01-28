package com.hadroncfy.bungeewhitelist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.lang.Thread;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class WhitelistCommand extends Command implements TabExecutor {
    private IWhitelist whitelist;

    public WhitelistCommand(IWhitelist whitelist) {
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
        if (args.length == 1) {
            if (args[0].equals("on")) {
                whitelist.enable();
                whitelist.broadcast(new TextComponent("White list is now enabled"));
                return;
            } else if (args[0].equals("off")) {
                whitelist.disable();
                whitelist.broadcast(new TextComponent("White list is now disabled"));
                return;
            } else if (args[0].equals("reload")) {
                whitelist.reload();
                whitelist.broadcast(new TextComponent("Reloaded white list"));
                return;
            } else if (args[0].equals("list")) {
                List<String> players = whitelist.list();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < players.size(); i++) {
                    if (i > 0)
                        sb.append(", ");
                    sb.append(players.get(i));
                }
                if (players.size() == 0) {
                    sender.sendMessage(new TextComponent("There're no players in the white list."));
                } else {
                    sender.sendMessage(new TextComponent(sb.toString()));
                }
                return;
            }
        } else if (args.length == 2) {
            if (args[0].equals("add")) {
                new Thread(() -> {
                    try {
                        Profile p = whitelist.createUUID(args[1]);
                        if (p != null){
                            whitelist.update(p.uuid, p.name);
                            sender.sendMessage(new TextComponent("Done"));
                            whitelist.broadcast(new TextComponent("Added " + p.name + "(" + p.uuid.toString() + ") to white list."));
                        }
                        else {
                            sender.sendMessage(new TextComponent("Player " + args[1] + " not found."));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        sender.sendMessage(new TextComponent("Failed to retrieve UUID."));
                    }
                }).start();
                return;
            }
            else if (args[0].equals("remove")){
                if (whitelist.removeByName(args[1])){
                    whitelist.broadcast(new TextComponent("Removed " + args[1] + " from white list."));
                }
                else {
                    sender.sendMessage(new TextComponent("Player not in the white list."));
                }
                return;
            }
        }
        sender.sendMessage(new TextComponent("Incorrect arguments."));
    }

}