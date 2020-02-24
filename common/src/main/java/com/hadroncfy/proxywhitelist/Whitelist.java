package com.hadroncfy.proxywhitelist;

import java.util.UUID;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class Whitelist {
    private Map<UUID, String> whitelist = new HashMap<>();
    private Config config = new Config();
    private Gson gson;

    private File dataPath;

    private IPlugin ctx;
    private WhitelistCommand wcmd = new WhitelistCommand(this);

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

    public IPlugin getContext(){ return ctx; }
    public WhitelistCommand getWhitelistCommand(){ return wcmd; }

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
                ctx.logger().info("Rejecting non-whitelisted player " + name);
                player.disconnect(config.kickMessage);
            }
        }
    }

    private File getWhitelistFile(){
        return new File(dataPath, "whitelist.json");
    }

    public boolean loadWhitelist(){
        File whitelistFile = getWhitelistFile();
        if (!whitelistFile.exists()){
            return saveWhitelist();
        }
        String content = Util.readFileSync(whitelistFile, ctx.logger());
        if (content != null){
            // Gson gson = new Gson();
            whitelist.clear();
            for (WhitelistEntry entry: gson.fromJson(content, WhitelistEntry[].class)){
                whitelist.put(UUID.fromString(entry.uuid), entry.name);
            }
            ctx.logger().info("Loaded white list");
            return true;
        }
        else {
            ctx.logger().error("Failed to load white list");
            return false;
        }
    }

    private boolean saveWhitelist(){
        File whitelistFile = getWhitelistFile();
        List<WhitelistEntry> wl = new ArrayList<>();
        for (Map.Entry<UUID, String> entry: whitelist.entrySet()){
            wl.add(new WhitelistEntry(entry.getKey().toString(), entry.getValue()));
        }
        return Util.writeFileSync(whitelistFile, gson.toJson(wl), ctx.logger());
    }

    public void update(UUID uuid, String name){
        whitelist.put(uuid, name);
        saveWhitelist();
    }

    private void remove(UUID uuid){
        whitelist.remove(uuid);
        saveWhitelist();
    }

	public List<String> getPlayers() {
        List<String> ret = new ArrayList<>();
        for (Map.Entry<UUID, String> entry: whitelist.entrySet()){
            ret.add(entry.getValue());
        }
		return ret;
	}
    
    private UUID getUUID(String name){
        for (Map.Entry<UUID, String> entry: whitelist.entrySet()){
            if (entry.getValue().equalsIgnoreCase(name)){
                return entry.getKey();
            }
        }
        return null;
    }

    public Profile createUUID(String name) throws IOException{
        return ctx.isOnlineMode() ? MinecraftAPI.getUUIDByName(name) : MinecraftAPI.getOfflineUUID(name);
    }

	public boolean removeByName(String name) {
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
    public void setEnabled(boolean e){
        boolean e1 = config.enabled;
        config.enabled = e;
        if (e1 != e){
            ctx.saveConfig(config);
        }
    }
}