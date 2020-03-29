package com.hadroncfy.proxywhitelist;

import java.util.UUID;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class Whitelist {
    private WhitelistStorage whitelist = new WhitelistStorage();
    private Config config = new Config();
    private final Gson gson = new GsonBuilder()
        .registerTypeAdapter(WhitelistStorage.class, WhitelistSerializer.getInstance())
        .registerTypeAdapter(UUID.class, UUIDTypeAdapter.getInstance())
        .setPrettyPrinting().create();

    private File dataPath;

    private IPlugin ctx;
    private final WhitelistCommand wcmd = new WhitelistCommand(this);

    public Whitelist(IPlugin plugin){
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
                if (!whitelist.get(uuid).name.equals(name)){
                    whitelist.get(uuid).name = name;
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
            whitelist = gson.fromJson(content, WhitelistStorage.class);
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
        return Util.writeFileSync(whitelistFile, gson.toJson(whitelist), ctx.logger());
    }

    public void update(UUID uuid, String name){
        Profile f = whitelist.get(uuid);
        if (f != null){
            f.name = name;
        }
        else {
            whitelist.put(new Profile(uuid, name));
        }
        saveWhitelist();
    }

    private void remove(UUID uuid){
        whitelist.remove(uuid);
        saveWhitelist();
    }

	public List<Profile> getPlayers() {
        List<Profile> ret = new ArrayList<>();
        whitelist.forEach((id, p) -> ret.add(p));
		return ret;
	}
    
    private UUID getUUID(String name){
        for (Map.Entry<UUID, Profile> entry: whitelist.entrySet()){
            if (entry.getValue().name.equalsIgnoreCase(name)){
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