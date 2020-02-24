package com.hadroncfy.proxywhitelist;

// import net.md_5.bungee.api.chat.TextComponent;
// import net.md_5.bungee.api.chat.BaseComponent;
// import net.md_5.bungee.api.ChatMessageType;
// import net.md_5.bungee.api.connection.PendingConnection;
// import net.md_5.bungee.api.event.LoginEvent;
// import net.md_5.bungee.api.plugin.Listener;
// import net.md_5.bungee.api.plugin.Plugin;
// import net.md_5.bungee.config.YamlConfiguration;
// import net.md_5.bungee.event.EventHandler;
// import net.md_5.bungee.config.Configuration;
// import net.md_5.bungee.api.connection.ProxiedPlayer;
import java.util.UUID;
import java.util.Map;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class Whitelist {
    private Map<UUID, String> whitelist = new HashMap<>();
    private Config config = new Config();
    private Gson gson;

    private File dataPath;

    private IPlugin ctx;

    public Whitelist(IPlugin plugin){
        gson = new GsonBuilder().setPrettyPrinting().create();
        ctx = plugin;
    }

    public void init(){
        dataPath = ctx.getDataDirectory();
        
        if (!dataPath.exists()){
            dataPath.mkdirs();
        }
        ctx.loadConfig(config);
        loadWhitelist();
    }

    public void checkPlayerJoin(IPlayer player){
        UUID uuid = player.getUUID();
        String name = player.getName();
        if (config.enabled){
            if (whitelist.get(uuid) != null){
                if (!whitelist.get(uuid).equals(name)){
                    whitelist.put(uuid, name);
                    saveWhitelist();
                }
            }
            else {
                ctx.getLogger().info("Rejecting non-whitelisted player " + name);
                player.disconnect(config.kickMessage);
            }
        }
    }
 
    // private void loadConfig(){
    //     File configFile = new File(dataPath, "config.yml");
    //     if (!configFile.exists()){
    //         saveConfig();
    //         return;
    //     }

    //     try {
    //         Configuration config = YamlConfiguration.getProvider(YamlConfiguration.class).load(configFile);
    //         enabled = config.getBoolean("enabled");
    //         kickMessage = config.getString("kick-message");
    //     }
    //     catch (Exception e) {
    //         ctx.getLogger().severe("Failed to load configuration!");
    //     }
    // }
 
    // private void saveConfig(){
    //     File configFile = new File(dataPath, "config.yml");
    //     try {
    //         Configuration config = new Configuration();
    //         config.set("enabled", enabled);
    //         config.set("kick-message", kickMessage);
    //         YamlConfiguration.getProvider(YamlConfiguration.class).save(config, configFile);
    //     }
    //     catch (Exception e) {
    //         ctx.getLogger().severe("Failed to save configuration!");
    //     }
    // }

    private void loadWhitelist(){
        File whitelistFile = new File(dataPath, "whitelist.json");
        if (!whitelistFile.exists()){
            saveWhitelist();
            return;
        }
        InputStream is = null;
		try {
			is = new FileInputStream(whitelistFile);
            String content = IOUtils.toString(is, Charsets.UTF_8);
            // Gson gson = new Gson();
            whitelist.clear();
            for (WhitelistEntry entry: gson.fromJson(content, WhitelistEntry[].class)){
                whitelist.put(UUID.fromString(entry.uuid), entry.name);
            }
            ctx.getLogger().info("Loaded white list");
		} catch (FileNotFoundException e) {
            ctx.getLogger().severe("File " + whitelistFile.toString() + " not found: " + e.getMessage());
        }
        catch (IOException e) {
            ctx.getLogger().severe("Cannot read white list file " + whitelistFile.toString() + ": " + e.getMessage());
        }
        finally {
            IOUtils.closeQuietly(is);
        }
    }

    private void saveWhitelist(){
        File whitelistFile = new File(dataPath, "whitelist.json");
        OutputStream os = null;
        try {
            os = new FileOutputStream(whitelistFile);
            List<WhitelistEntry> wl = new ArrayList<>();
            for (Map.Entry<UUID, String> entry: whitelist.entrySet()){
                wl.add(new WhitelistEntry(entry.getKey().toString(), entry.getValue()));
            }
            // Gson gson = new Gson();
            String content = gson.toJson(wl);
            OutputStreamWriter w = new OutputStreamWriter(os);
            w.append(content);
            w.close();
		} catch (FileNotFoundException e) {
			ctx.getLogger().severe("File " + whitelistFile.toString() + " not found: " + e.getMessage());
		} catch (IOException e){
            ctx.getLogger().severe("Cannot write file " + whitelistFile.toString() + ": " + e.getMessage());
        }
        finally {
            IOUtils.closeQuietly(os);
        }
    }

    private void update(UUID uuid, String name){
        whitelist.put(uuid, name);
        saveWhitelist();
    }

    private void remove(UUID uuid){
        whitelist.remove(uuid);
        saveWhitelist();
    }

	public List<String> list() {
        List<String> ret = new ArrayList<>();
        for (Map.Entry<UUID, String> entry: whitelist.entrySet()){
            ret.add(entry.getValue());
        }
		return ret;
	}

	// @Override
	// public void enable() {
    //     enabled = true;
    //     saveConfig();
	// }

	// @Override
	// public void disable() {
    //     enabled = false;
    //     saveConfig();
    // }
    
    private UUID getUUID(String name){
        for (Map.Entry<UUID, String> entry: whitelist.entrySet()){
            if (entry.getValue().equalsIgnoreCase(name)){
                return entry.getKey();
            }
        }
        return null;
    }

    private Profile createUUID(String name) throws IOException{
        return ctx.isOnlineMode() ? MinecraftAPI.getUUIDByName(name) : MinecraftAPI.getOfflineUUID(name);
    }

	private boolean removeByName(String name) {
        UUID uuid = getUUID(name);
        if (uuid != null){
            remove(uuid);
            return true;
        }
        else {
            return false;
        }
	}

    // @Override
    // public void broadcast(BaseComponent c){
    //     for (ProxiedPlayer player: getProxy().getPlayers()){
    //         player.sendMessage(ChatMessageType.CHAT, c);
    //     }
    // }

    public void execCommand(ICommandSender sender, String[] args){
        if (args.length == 1) {
            if (args[0].equals("on")) {
                config.enabled = true;
                ctx.saveConfig(config);
                ctx.broadcast(sender, "White list is now enabled");
                return;
            } else if (args[0].equals("off")) {
                config.enabled = false;
                ctx.saveConfig(config);
                ctx.broadcast(sender, "White list is now disabled");
                return;
            } else if (args[0].equals("reload")) {
                loadWhitelist();
                ctx.broadcast(sender, "Reloaded white list");
                return;
            } else if (args[0].equals("list")) {
                List<String> players = list();
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
                        Profile p = createUUID(args[1]);
                        if (p != null){
                            update(p.uuid, p.name);
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
                if (removeByName(args[1])){
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