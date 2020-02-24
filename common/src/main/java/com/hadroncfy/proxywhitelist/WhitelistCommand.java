package com.hadroncfy.proxywhitelist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WhitelistCommand {
    private Whitelist whitelist;

    WhitelistCommand(Whitelist w){
        whitelist = w;
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

    public List<String> doCompletion(String[] args) {
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
            if (args[0].equals("remove")){
                ret = filterStart(whitelist.getPlayers(), args[1]);
            }
        }

        return ret;
    }

    public void exec(ICommandSender sender, String[] args){
        IPlugin ctx = whitelist.getContext();
        if (args.length == 1) {
            if (args[0].equals("on")) {
                whitelist.setEnabled(true);
                ctx.broadcast(sender, "White list is now enabled");
                return;
            } else if (args[0].equals("off")) {
                whitelist.setEnabled(false);
                ctx.broadcast(sender, "White list is now disabled");
                return;
            } else if (args[0].equals("reload")) {
                whitelist.loadWhitelist();
                ctx.broadcast(sender, "Reloaded white list");
                return;
            } else if (args[0].equals("list")) {
                List<String> players = whitelist.getPlayers();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < players.size(); i++) {
                    if (i > 0)
                        sb.append(", ");
                    sb.append(players.get(i));
                }
                if (players.size() == 0) {
                    sender.sendResultMessage("There're no players in the white list.");
                } else {
                    sender.sendResultMessage(sb.toString());
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
                            sender.sendResultMessage("Done");
                            ctx.broadcast(sender, "Added " + p.name + "(" + p.uuid.toString() + ") to white list.");
                        }
                        else {
                            sender.sendResultMessage("Player " + args[1] + " not found.");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        sender.sendResultMessage("Failed to retrieve UUID.");
                    }
                }).start();
                return;
            }
            else if (args[0].equals("remove")){
                if (whitelist.removeByName(args[1])){
                    ctx.broadcast(sender, "Removed " + args[1] + " from white list.");
                }
                else {
                    sender.sendResultMessage("Player not in the white list.");
                }
                return;
            }
        }
        sender.sendResultMessage("Incorrect arguments.");
    }
}